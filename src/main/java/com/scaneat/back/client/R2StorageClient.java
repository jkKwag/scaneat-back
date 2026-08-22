package com.scaneat.back.client;

import com.scaneat.back.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

// Cloudflare R2(S3 호환)에 이미지를 업로드한다. 버킷은 하나(scimg)고, 용도별 폴더
// (biz-cert/menu-image/seat-image)로만 구분한다. 버킷에 Public Access(커스텀 도메인)가
// 켜져 있어 업로드 직후 바로 공개 URL로 접근 가능하다.
@Component
public class R2StorageClient {

	public enum Folder {
		BIZ_CERT("biz-cert"),
		MENU_IMAGE("menu-image"),
		SEAT_IMAGE("seat-image");

		private final String prefix;

		Folder(String prefix) {
			this.prefix = prefix;
		}
	}

	private final S3Client s3Client;
	private final String bucket;
	private final String publicUrlBase;

	public R2StorageClient(
			S3Client s3Client,
			@Value("${r2.bucket}") String bucket,
			@Value("${r2.public-url}") String publicUrl) {
		this.s3Client = s3Client;
		this.bucket = bucket;
		this.publicUrlBase = publicUrl.endsWith("/") ? publicUrl.substring(0, publicUrl.length() - 1) : publicUrl;
	}

	public String upload(Folder folder, String path, byte[] content, String contentType) {
		String key = folder.prefix + "/" + path;
		try {
			s3Client.putObject(
					PutObjectRequest.builder()
							.bucket(bucket)
							.key(key)
							.contentType(contentType)
							.build(),
					RequestBody.fromBytes(content));
			return publicUrl(folder, path);
		} catch (S3Exception ex) {
			throw new BusinessException(HttpStatus.valueOf(ex.statusCode()), "이미지 업로드에 실패했습니다: " + ex.getMessage());
		}
	}

	public String publicUrl(Folder folder, String path) {
		return publicUrlBase + "/" + folder.prefix + "/" + path;
	}
}

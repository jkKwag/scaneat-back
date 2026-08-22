package com.scaneat.back.client;

import com.scaneat.back.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

// Cloudflare R2(S3 호환 API)에 이미지를 업로드한다. 버킷 3개(사업자등록증/메뉴/좌석) 모두
// Public Access가 켜져 있어, 업로드 직후 바로 공개 URL로 접근 가능하다.
@Component
public class R2StorageClient {

	public enum Bucket { BIZ_CERT, MENU_IMAGE, SEAT_IMAGE }

	private final S3Client s3Client;
	private final String bizCertBucket;
	private final String menuImageBucket;
	private final String seatImageBucket;
	private final String bizCertPublicUrl;
	private final String menuImagePublicUrl;
	private final String seatImagePublicUrl;

	public R2StorageClient(
			S3Client s3Client,
			@Value("${r2.bucket.biz-cert}") String bizCertBucket,
			@Value("${r2.bucket.menu-image}") String menuImageBucket,
			@Value("${r2.bucket.seat-image}") String seatImageBucket,
			@Value("${r2.public-url.biz-cert}") String bizCertPublicUrl,
			@Value("${r2.public-url.menu-image}") String menuImagePublicUrl,
			@Value("${r2.public-url.seat-image}") String seatImagePublicUrl) {
		this.s3Client = s3Client;
		this.bizCertBucket = bizCertBucket;
		this.menuImageBucket = menuImageBucket;
		this.seatImageBucket = seatImageBucket;
		this.bizCertPublicUrl = stripTrailingSlash(bizCertPublicUrl);
		this.menuImagePublicUrl = stripTrailingSlash(menuImagePublicUrl);
		this.seatImagePublicUrl = stripTrailingSlash(seatImagePublicUrl);
	}

	public String upload(Bucket bucket, String path, byte[] content, String contentType) {
		try {
			s3Client.putObject(
					PutObjectRequest.builder()
							.bucket(bucketName(bucket))
							.key(path)
							.contentType(contentType)
							.build(),
					RequestBody.fromBytes(content));
			return publicUrl(bucket, path);
		} catch (S3Exception ex) {
			throw new BusinessException(HttpStatus.valueOf(ex.statusCode()), "이미지 업로드에 실패했습니다: " + ex.getMessage());
		}
	}

	// 업로드 없이, 이미 저장된 경로로부터 공개 URL만 다시 만들어낼 때 사용한다.
	public String publicUrl(Bucket bucket, String path) {
		return publicUrlBase(bucket) + "/" + path;
	}

	private String bucketName(Bucket bucket) {
		return switch (bucket) {
			case BIZ_CERT -> bizCertBucket;
			case MENU_IMAGE -> menuImageBucket;
			case SEAT_IMAGE -> seatImageBucket;
		};
	}

	private String publicUrlBase(Bucket bucket) {
		return switch (bucket) {
			case BIZ_CERT -> bizCertPublicUrl;
			case MENU_IMAGE -> menuImagePublicUrl;
			case SEAT_IMAGE -> seatImagePublicUrl;
		};
	}

	private static String stripTrailingSlash(String url) {
		return url != null && url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
	}
}

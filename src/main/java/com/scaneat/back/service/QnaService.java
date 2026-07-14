package com.scaneat.back.service;

import com.scaneat.back.common.exception.ResourceNotFoundException;
import com.scaneat.back.dto.qna.QnaAnswerRequest;
import com.scaneat.back.dto.qna.QnaRequest;
import com.scaneat.back.dto.qna.QnaResponse;
import com.scaneat.back.entity.Qna;
import com.scaneat.back.repository.QnaRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QnaService {

	private final QnaRepository qnaRepository;

	public List<QnaResponse> getQnas() {
		return qnaRepository.findAllByOrderByCreatedAtDesc().stream()
				.map(QnaResponse::from)
				.toList();
	}

	@Transactional
	public QnaResponse createQna(QnaRequest request) {
		Qna qna = Qna.builder()
				.question(request.question())
				.bizRegNo(request.bizRegNo())
				.isPublic(request.isPublic() != null ? request.isPublic() : Boolean.TRUE)
				.createdAt(LocalDateTime.now())
				.build();
		return QnaResponse.from(qnaRepository.save(qna));
	}

	@Transactional
	public QnaResponse answerQna(Long id, QnaAnswerRequest request) {
		Qna qna = qnaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("QnA를 찾을 수 없습니다: " + id));
		qna.setAnswer(request.answer());
		qna.setAnsweredAt(LocalDateTime.now());
		return QnaResponse.from(qna);
	}
}

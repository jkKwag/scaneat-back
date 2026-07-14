package com.scaneat.back.repository;

import com.scaneat.back.entity.UsrChatMsg;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsrChatMsgRepository extends JpaRepository<UsrChatMsg, Long> {

	List<UsrChatMsg> findByRsvnNoOrderByCreatedAtAsc(String rsvnNo);
}

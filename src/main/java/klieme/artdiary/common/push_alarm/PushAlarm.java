package klieme.artdiary.common.push_alarm;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

import klieme.artdiary.fcm.FcmSendDto;

@Component
public class PushAlarm {
	public void sendMessageTo(FcmSendDto fcmSendDto) {
		// type
		String title = fcmSendDto.getTitle();

		title += "/" + fcmSendDto.getType();
		// id
		if (Objects.equals(fcmSendDto.getType(), "exhibition")) {
			title += "-" + fcmSendDto.getExhId().toString();
		} else if (Objects.equals(fcmSendDto.getType(), "gathering")) {
			title += "-" + fcmSendDto.getGatherId().toString();
		}

		Message message = Message.builder()
			.putData("title", title)
			.putData("body", fcmSendDto.getBody())
			.setToken(fcmSendDto.getToken())
			.build();
		try {
			FirebaseMessaging.getInstance().send(message);
		} catch (FirebaseMessagingException e) {
			System.out.println("Wrong Token");
		}
	}
}

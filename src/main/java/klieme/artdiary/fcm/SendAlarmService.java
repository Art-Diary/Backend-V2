package klieme.artdiary.fcm;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import klieme.artdiary.common.push_alarm.PushAlarm;
import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.like_exh.data_access.repository.LikeExhRepository;
import klieme.artdiary.gathering.data_access.entity.GatheringEntity;
import klieme.artdiary.record_data_access.repository.VisitExhRepository;
import klieme.artdiary.user.data_access.entity.UserEntity;
import klieme.artdiary.user.enums.NotiType;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SendAlarmService {

	private final LikeExhRepository likeExhRepository;
	private final VisitExhRepository visitExhRepository;
	private final PushAlarm pushAlarm;

	@Autowired
	public SendAlarmService(LikeExhRepository likeExhRepository, VisitExhRepository visitExhRepository,
		PushAlarm pushAlarm) {
		this.likeExhRepository = likeExhRepository;
		this.visitExhRepository = visitExhRepository;
		this.pushAlarm = pushAlarm;
	}

	public void sendMessageAboutExh() {
		log.info("[알림 보내기]");
		List<FcmSendDto> fcmSendDtoList = new ArrayList<>();
		// 좋아요한 전시회 시작일/마감일 알림
		aboutFavorite(fcmSendDtoList, NotiType.LIKE_EXH_NOTI.label());
		// 혼자 가는 전시회 날짜 알림
		aboutVisitSoloDate(fcmSendDtoList, NotiType.SOLO_VISIT_NOTI.label());
		// 모임에서 가는 전시회 날짜 알림
		aboutVisitGatheringDate(fcmSendDtoList, NotiType.GATHERING_VISIT_NOTI.label());
		// push
		for (FcmSendDto fcmSendDto : fcmSendDtoList) {
			try {
				System.out.println(fcmSendDto.getBody() + " " + fcmSendDto.getToken());
				pushAlarm.sendMessageTo(fcmSendDto);
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}

	private void aboutFavorite(List<FcmSendDto> fcmSendDtoList, Long notiId) {
		// 좋아요한 전시회 시작일/마감일 알림
		List<Map<String, Object>> likeExhInfoList = likeExhRepository.getLikeExhWithUserAndExh(notiId);
		LocalDate localDate = LocalDate.now();

		for (Map<String, Object> info : likeExhInfoList) {
			UserEntity userEntity = (UserEntity)info.get("user");
			ExhEntity exhEntity = (ExhEntity)info.get("exhibition");
			String title = null;
			String body = null;

			if (localDate.isEqual(exhEntity.getStartDate())) {
				title = "오늘은 좋아요한 전시회 시작일";
				body = "\"" + exhEntity.getExhName() + "\" 전시회 정보를 보려면 눌러주세요!";
			} else if (localDate.isEqual(exhEntity.getEndDate())) {
				title = "오늘은 좋아요한 전시회 종료일";
				body = "\"" + exhEntity.getExhName() + "\" 전시회 정보를 보려면 눌러주세요!";
			}
			if (title != null) {
				fcmSendDtoList.add(FcmSendDto.builder()
					.token(userEntity.getAlarmToken())
					.title(title)
					.body(body)
					.type("exhibition")
					.exhId(exhEntity.getExhId())
					.build());
			}
		}
	}

	private void aboutVisitSoloDate(List<FcmSendDto> fcmSendDtoList, Long notiId) {
		// 혼자 가는 전시회 날짜 알림
		List<Map<String, Object>> visitInfoList = visitExhRepository.getVisitSoloDateForFcm(notiId);

		for (Map<String, Object> visitInfo : visitInfoList) {
			UserEntity user = (UserEntity)visitInfo.get("user");
			ExhEntity exh = (ExhEntity)visitInfo.get("exhibition");

			fcmSendDtoList.add(FcmSendDto.builder()
				.token(user.getAlarmToken())
				.title("오늘은 혼자 전시회 가는 날")
				.body("\"" + exh.getExhName() + "\" 전시회 정보를 보려면 눌러주세요!")
				.type("exhibition")
				.exhId(exh.getExhId())
				.build());
		}
	}

	private void aboutVisitGatheringDate(List<FcmSendDto> fcmSendDtoList, Long notiId) {
		// 모임에서 가는 전시회 날짜 알림
		List<Map<String, Object>> visitInfoList = visitExhRepository.getVisitGatheringDateForFcm(notiId);

		for (Map<String, Object> visitInfo : visitInfoList) {
			UserEntity user = (UserEntity)visitInfo.get("user");
			ExhEntity exh = (ExhEntity)visitInfo.get("exhibition");
			GatheringEntity gathering = (GatheringEntity)visitInfo.get("gathering");

			fcmSendDtoList.add(FcmSendDto.builder()
				.token(user.getAlarmToken())
				.title("오늘은 \"" + gathering.getGatheringName() + "\"이랑 전시회 가는 날")
				.body("\"" + exh.getExhName() + "\" 전시회 정보를 보려면 눌러주세요!")
				.type("exhibition")
				.exhId(exh.getExhId())
				.build());
		}
	}
}

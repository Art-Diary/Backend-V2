package klieme.artdiary.gathering.service;

import static klieme.artdiary.common.FormatDate.*;
import static klieme.artdiary.common.SecurityUtil.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import klieme.artdiary.calendar.enums.CalendarKind;
import klieme.artdiary.common.api.ArtDiaryException;
import klieme.artdiary.common.api.MessageType;
import klieme.artdiary.common.push_alarm.PushAlarm;
import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.repository.ExhRepository;
import klieme.artdiary.gathering.data_access.entity.GatheringEntity;
import klieme.artdiary.gathering.data_access.entity.GatheringExhPresenceEntity;
import klieme.artdiary.gathering.data_access.entity.GatheringExhPresenceId;
import klieme.artdiary.gathering.data_access.entity.GatheringMemberEntity;
import klieme.artdiary.gathering.data_access.entity.GatheringMemberId;
import klieme.artdiary.gathering.data_access.repository.GatheringExhPresenceRepository;
import klieme.artdiary.gathering.data_access.repository.GatheringMemberRepository;
import klieme.artdiary.gathering.data_access.repository.GatheringRepository;
import klieme.artdiary.gathering.info.ExhibitionInfo;
import klieme.artdiary.gathering.info.MateInfo;
import klieme.artdiary.gathering.info.VisitExhInfo;
import klieme.artdiary.record_data_access.entity.VisitExhEntity;
import klieme.artdiary.record_data_access.repository.VisitExhRepository;
import klieme.artdiary.user.data_access.entity.UserEntity;
import klieme.artdiary.user.data_access.repository.UserRepository;

@Service
public class GatheringService implements GatheringOperationUseCase, GatheringReadUseCase {
	private final GatheringRepository gatheringRepository;
	private final GatheringMemberRepository gatheringMemberRepository;
	private final ExhRepository exhRepository;
	private final UserRepository userRepository;
	private final VisitExhRepository visitExhRepository;
	private final GatheringExhPresenceRepository gatheringExhPresenceRepository;
	private final PushAlarm pushAlarm;

	@Autowired
	public GatheringService(GatheringRepository gatheringRepository, PushAlarm pushAlarm,
		GatheringMemberRepository gatheringMemberRepository, ExhRepository exhRepository, UserRepository userRepository,
		VisitExhRepository visitExhRepository, GatheringExhPresenceRepository gatheringExhPresenceRepository) {
		this.gatheringRepository = gatheringRepository;
		this.gatheringMemberRepository = gatheringMemberRepository;
		this.exhRepository = exhRepository;
		this.userRepository = userRepository;
		this.pushAlarm = pushAlarm;
		this.visitExhRepository = visitExhRepository;
		this.gatheringExhPresenceRepository = gatheringExhPresenceRepository;
	}

	@Transactional
	@Override
	public GatheringReadUseCase.FindGatheringResult createGathering(GatheringCreateCommand command) {
		// 모임 생성
		GatheringEntity gatheringEntity = GatheringEntity.builder()
			.gatheringName(command.getGatheringName())
			.build();
		gatheringRepository.save(gatheringEntity);
		// 모임에 유저 추가
		GatheringMemberEntity mateEntity = GatheringMemberEntity.builder()
			.gatheringMemberId(GatheringMemberId.builder()
				.gatheringId(gatheringEntity.getGatheringId())
				.userId(getUserId())
				.build())
			.build();
		gatheringMemberRepository.save(mateEntity);
		// 반환
		return GatheringReadUseCase.FindGatheringResult.findByGathering(gatheringEntity);
	}

	@Override
	public List<GatheringReadUseCase.FindGatheringResult> getGatheringList() {
		// userId: getUserId(), exhId: query.getExhId(), gatherId: query.getGatherId()
		Long userId = getUserId();
		List<GatheringReadUseCase.FindGatheringResult> gatherings = new ArrayList<>();

		// 사용자가 모임에서 최근에 전시회를 방문한 날짜 순으로 정렬됨.
		List<GatheringEntity> gatheringEntityList = gatheringMemberRepository.getGatheringListByRecentVisitDate(userId);

		for (GatheringEntity gathering : gatheringEntityList) {
			gatherings.add(GatheringReadUseCase.FindGatheringResult.findByGathering(gathering));
		}
		return gatherings;
	}

	@Override
	public FindGatheringDetailInfoResult getGatheringDetailInfo(Long gatheringId) {
		// 유저가 모임에 포함되어있는지 확인
		gatheringMemberRepository.findByGatheringMemberId(GatheringMemberId.builder()
			.userId(getUserId())
			.gatheringId(gatheringId)
			.build()).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		List<MateInfo> mateInfoList = new ArrayList<>();
		List<ExhibitionInfo> exhibitionInfoList = new ArrayList<>();
		// 1. gathering에 포함되어 있는 유저 리스트
		List<UserEntity> gatheringMateList = gatheringMemberRepository.getGatheringMateList(gatheringId);

		for (UserEntity info : gatheringMateList) {
			mateInfoList.add(MateInfo.builder()
				.userId(info.getUserId())
				.nickname(info.getNickname())
				.build());
		}
		// 2. gathering이 저장한 전시회 리스트(중복 제외)
		List<Map<String, Object>> gatheringVisitExhList = visitExhRepository.getGatheringVisitExhListWithExhInfo(
			gatheringId);

		for (Map<String, Object> info : gatheringVisitExhList) {
			ExhEntity exhibition = (ExhEntity)info.get("exhibition");
			LocalDate visitDate = (LocalDate)info.get("visitDate");

			exhibitionInfoList.add(ExhibitionInfo.builder()
				.exhId(exhibition.getExhId())
				.exhName(exhibition.getExhName())
				.poster(exhibition.getPoster())
				.gallery(exhibition.getGallery())
				.visitDate(changeDateFormat(visitDate))
				.build());
		}
		return FindGatheringDetailInfoResult.findByGatheringDetailInfo(mateInfoList, exhibitionInfoList);
	}

	@Override
	public FindIsGatheringMemberResult searchNicknameNotInGathering(GatheringNicknameFindQuery query) {
		Long myUserId = getUserId();
		// 모임에 속해 있는지 확인
		gatheringMemberRepository.findByGatheringMemberId(GatheringMemberId.builder()
				.gatheringId(query.getGatheringId())
				.userId(myUserId)
				.build())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		// 내 전시 리스트 중, 이미 모임에 포함된 경우와 아닌 경우로 나눠서 반환하기
		List<Map<String, Object>> gatheringMateQuery = gatheringMemberRepository.getGatheringMateListForSearch(
			query.getGatheringId(), myUserId, query.getNickname());
		List<FindGatheringMemberResult> alreadyMate = new ArrayList<>();
		List<FindGatheringMemberResult> notMate = new ArrayList<>();

		for (Map<String, Object> gatheringMate : gatheringMateQuery) {
			UserEntity user = (UserEntity)gatheringMate.get("userEntity");
			Boolean isMate = (Boolean)gatheringMate.get("isGatheringMate");

			if (Objects.equals(myUserId, user.getUserId())) {
				continue;
			}
			if (isMate) {
				alreadyMate.add(FindGatheringMemberResult.findByGatheringMates(user));
			} else {
				notMate.add(FindGatheringMemberResult.findByGatheringMates(user));
			}
		}
		return FindIsGatheringMemberResult.findByGatheringMate(alreadyMate, notMate);
	}

	@Transactional
	@Override
	public List<FindGatheringMemberResult> addGatheringMate(GatheringMateCreateCommand command) {
		Long myUserId = getUserId();
		// 유저가 존재하는지 확인
		UserEntity requestGatheringMate = getUser(command.getUserId());
		// 모임에 속해 있는지 확인
		gatheringMemberRepository.findByGatheringMemberId(
				GatheringMemberId.builder().gatheringId(command.getGatheringId()).userId(myUserId).build())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		// 모임에 추가
		GatheringMemberEntity gatheringMate = GatheringMemberEntity.builder()
			.gatheringMemberId(GatheringMemberId.builder()
				.userId(requestGatheringMate.getUserId())
				.gatheringId(command.getGatheringId())
				.build())
			.build();
		try {
			gatheringMemberRepository.save(gatheringMate);
		} catch (Exception e) {
			throw new ArtDiaryException(MessageType.CONFLICT);
		}
		// 모임에 속해 있는 메이트 리스트 조회
		List<UserEntity> gatheringMateList = gatheringMemberRepository.getGatheringMateList(command.getGatheringId());
		List<FindGatheringMemberResult> results = new ArrayList<>();

		for (UserEntity info : gatheringMateList) {
			results.add(FindGatheringMemberResult.findByGatheringMates(info));
		}
		// 초대 받은 사용자에게 푸시 알림 보내기
		// sendPushAlarmToInvitedUser(requestGatheringMate, savedGathering);
		return results;
	}

	@Transactional
	@Override
	public void deleteMyGathering(Long gatheringId) {

		GatheringMemberId deleteGatheringMemberId = GatheringMemberId.builder()
			.gatheringId(gatheringId)
			.userId(getUserId())
			.build();
		GatheringMemberEntity deleteEntity = gatheringMemberRepository.findByGatheringMemberId(deleteGatheringMemberId)
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		gatheringMemberRepository.delete(deleteEntity);
	}

	@Override
	public List<FindGatheringVisitExhResult> getGatheringVisitDateList(GatheringVisitExhFindQuery query) {
		Long userId = getUserId();
		// 모임에 속해 있는지 확인
		gatheringMemberRepository.findByGatheringMemberId(
				GatheringMemberId.builder().gatheringId(query.getGatheringId()).userId(userId).build())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		//
		LocalDate selectedStartDate = LocalDate.of(query.getYear(), query.getMonth(), 1);
		LocalDate selectedEndDate = selectedStartDate.withDayOfMonth(selectedStartDate.lengthOfMonth());
		List<Map<String, Object>> visitInfo = visitExhRepository.getVisitInfoForCalendar(CalendarKind.GATHER, userId,
			query.getGatheringId(), selectedStartDate, selectedEndDate);
		HashMap<Integer, List<VisitExhInfo>> dayOfScheduleInfos = new HashMap<>();

		for (Map<String, Object> info : visitInfo) {
			VisitExhEntity visitExh = (VisitExhEntity)info.get("visitExh");
			ExhEntity exhibition = (ExhEntity)info.get("exhibition");

			// 날짜 별 전시회 추가
			int day = visitExh.getVisitDate().getDayOfMonth();

			dayOfScheduleInfos.computeIfAbsent(day, k -> new ArrayList<>());
			dayOfScheduleInfos.get(day).add(VisitExhInfo.builder()
				.exhId(exhibition.getExhId())
				.exhName(exhibition.getExhName())
				.gallery(exhibition.getGallery())
				.poster(exhibition.getPoster())
				.startDate(changeDateFormat(exhibition.getStartDate()))
				.endDate(changeDateFormat(exhibition.getEndDate()))
				.visitDate(changeDateFormat(visitExh.getVisitDate()))
				.build());
		}
		List<FindGatheringVisitExhResult> results = new ArrayList<>();

		for (int day = 1; day <= selectedEndDate.getDayOfMonth(); day++) {
			if (dayOfScheduleInfos.get(day) != null) {
				results.add(FindGatheringVisitExhResult.findByGatheringVisitExh(day, dayOfScheduleInfos.get(day)));
			} else {
				results.add(FindGatheringVisitExhResult.findByGatheringVisitExh(day, null));
			}
		}
		return results;
	}

	@Override
	public List<FindGatheringNotVisitExhResult> getGatheringNotVisitedExhListWithDate(
		GatheringNotVisitExhFindQuery query) {
		Long userId = getUserId();
		// 모임에 속해 있는지 확인
		gatheringMemberRepository.findByGatheringMemberId(
				GatheringMemberId.builder().gatheringId(query.getGatheringId()).userId(userId).build())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		// 모임에서 가지 않은 전시회 목록 조회
		List<FindGatheringNotVisitExhResult> results = new ArrayList<>();
		List<ExhEntity> infoList = exhRepository.getNotVisitedExhListWithDateInGathering(query.getDate(),
			query.getGatheringId());

		for (ExhEntity exhibition : infoList) {
			results.add(FindGatheringNotVisitExhResult.findByGatheringNotVisitExhResult(exhibition));
		}
		return results;
	}

	@Transactional
	@Override
	public void addExhAboutGathering(ExhGatheringCreateCommand command) {
		Long userId = getUserId();
		// 모임에 속해 있는지 확인
		gatheringMemberRepository.findByGatheringMemberId(
				GatheringMemberId.builder().gatheringId(command.getGatheringId()).userId(userId).build())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		// exhId 존재하는 전시회 아이디인지 확인
		ExhEntity storedExhEntity = exhRepository.findByExhId(command.getExhId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		// 전시회 일정에 맞춰 갈 수 있는지 확인
		if (storedExhEntity.getStartDate().isAfter(command.getVisitDate())
			|| storedExhEntity.getEndDate().isBefore(command.getVisitDate())) {
			throw new ArtDiaryException(MessageType.FORBIDDEN_DATE);
		}
		// 관람 날짜 중복 확인
		Boolean checkExhVisit = visitExhRepository.existsByExhIdAndUserIdAndGatheringIdAndVisitDate(command.getExhId(),
			null, command.getGatheringId(), command.getVisitDate());

		if (checkExhVisit) {
			throw new ArtDiaryException(MessageType.CONFLICT);
		}
		// 관람 날짜 추가
		VisitExhEntity newExhVisit = VisitExhEntity.builder()
			.exhId(command.getExhId())
			.gatheringId(command.getGatheringId())
			.visitDate(command.getVisitDate())
			.build();
		try {
			visitExhRepository.save(newExhVisit);
		} catch (DataIntegrityViolationException e) {
			throw new ArtDiaryException(MessageType.CONFLICT);
		}
		// 모임 리스트
		List<GatheringMemberEntity> gatheringMemberList = gatheringMemberRepository.findByGatheringMemberIdGatheringId(
			command.getGatheringId());
		List<GatheringExhPresenceEntity> gatheringExhPresenceList = new ArrayList<>();

		for (GatheringMemberEntity member : gatheringMemberList) {
			gatheringExhPresenceList.add(GatheringExhPresenceEntity.builder()
				.gatheringExhPresenceId(GatheringExhPresenceId.builder()
					.exhId(command.getExhId())
					.gatheringId(command.getGatheringId())
					.userId(member.getGatheringMemberId().getUserId())
					.build())
				.build());
		}
		gatheringExhPresenceRepository.saveAll(gatheringExhPresenceList);
		// 모임 멤버들에게 푸시 알림 보내기
		// sendPushAlarmToGatheringMember(command.getGatherId(), command.getVisitDate());
	}

	private Long getUserId() {
		return getCurrentUserEntity().getUserId();
	}

	private UserEntity getUser(Long userId) {
		return userRepository.findByUserId(userId).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
	}

	private void sendPushAlarmToInvitedUser(UserEntity invitedUser, GatheringEntity gathering) throws IOException {
		// 초대받는 사람의 알림이 켜져있어야 하고, 알림 토큰이 있어야 한다.
		// if (invitedUser.getNewGatheringAlarm() && invitedUser.getAlarmToken() != null) {
		// 	pushAlarm.sendMessageTo(FcmSendDto.builder().token(invitedUser.getAlarmToken())
		// 		.title("새로운 모임 초대 알림")
		// 		.body("\"" + gathering.getGatherName() + "\"에서 초대했어요. 모임 정보를 보려면 눌러주세요!")
		// 		.type("gathering")
		// 		.gatherId(gathering.getGatherId())
		// 		.build());
		// }
	}

	private void sendPushAlarmToGatheringMember(Long gatherId, LocalDate visitDate) throws
		IOException {
		// 모임의 멤버들의 NewGatheringAlarm 푸시 알림이 켜져있어야 하고, 알림 토큰이 있어야 한다.
		// List<Map<String, Object>> gatheringMateList = gatheringMemberRepository.getGatheringMateList(gatherId);
		// String date = changeDateFormat(visitDate);
		//
		// for (Map<String, Object> info : gatheringMateList) {
		// 	UserEntity user = (UserEntity)info.get("user");
		// 	GatheringEntity gathering = (GatheringEntity)info.get("gathering");
		//
		// 	if (Objects.equals(user.getUserId(), getUserId())) {
		// 		continue;
		// 	}
		// 	// if (user.getNewDateGatheringAlarm() && user.getAlarmToken() != null) {
		// 	// 	pushAlarm.sendMessageTo(FcmSendDto.builder().token(user.getAlarmToken())
		// 	// 		.title("\"" + gathering.getGatherName() + "\"" + "와 함께 보러 갈 새로운 일정 알림")
		// 	// 		.body(date + "에 전시회 관람 날짜가 추가됐어요. 일정을 확인하려면 눌러주세요.")
		// 	// 		.type("calendar")
		// 	// 		.build());
		// 	// }
		// }
	}
}

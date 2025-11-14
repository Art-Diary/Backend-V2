package klieme.artdiary.user.service;

import static klieme.artdiary.common.SecurityUtil.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import klieme.artdiary.common.api.ArtDiaryException;
import klieme.artdiary.common.api.MessageType;
import klieme.artdiary.common.image.ImageType;
import klieme.artdiary.common.image.S3ImageTransfer;
import klieme.artdiary.common.jwt.JwtUtil;
import klieme.artdiary.common.jwt.TokenInfo;
import klieme.artdiary.exhibition.data_access.entity.RegExhEntity;
import klieme.artdiary.exhibition.data_access.repository.RegExhRepository;
import klieme.artdiary.record_data_access.entity.DiaryEntity;
import klieme.artdiary.record_data_access.entity.ExhVisitEntity;
import klieme.artdiary.record_data_access.repository.DiaryRepository;
import klieme.artdiary.record_data_access.repository.ExhVisitRepository;
import klieme.artdiary.user.data_access.entity.NotificationTypeEntity;
import klieme.artdiary.user.data_access.entity.ReasonEntity;
import klieme.artdiary.user.data_access.entity.SocialLoginEntity;
import klieme.artdiary.user.data_access.entity.SocialLoginId;
import klieme.artdiary.user.data_access.entity.UserEntity;
import klieme.artdiary.user.data_access.entity.UserNotificationSettingEntity;
import klieme.artdiary.user.data_access.entity.UserNotificationSettingId;
import klieme.artdiary.user.data_access.repository.NotificationTypeRepository;
import klieme.artdiary.user.data_access.repository.ReasonRepository;
import klieme.artdiary.user.data_access.repository.SocialLoginRepository;
import klieme.artdiary.user.data_access.repository.UserNotificationSettingRepository;
import klieme.artdiary.user.data_access.repository.UserRepository;
import klieme.artdiary.user.dto.NotiInfo;
import klieme.artdiary.user.enums.RoleType;

@Service
public class UserService implements UserOperationUseCase, UserReadUseCase {

	private final UserRepository userRepository;
	private final ExhVisitRepository exhVisitRepository;
	private final DiaryRepository diaryRepository;
	private final ReasonRepository reasonRepository;
	private final SocialLoginRepository socialLoginRepository;
	private final RegExhRepository regExhRepository;
	private final JwtUtil jwtUtil;
	private final S3ImageTransfer s3ImageTransfer;
	private final UserNotificationSettingRepository userNotificationSettingRepository;
	private final NotificationTypeRepository notificationTypeRepository;

	@Autowired
	public UserService(UserRepository userRepository, ExhVisitRepository exhVisitRepository,
		DiaryRepository diaryRepository, ReasonRepository reasonRepository, SocialLoginRepository socialLoginRepository,
		RegExhRepository regExhRepository, JwtUtil jwtUtil, S3ImageTransfer s3ImageTransfer,
		UserNotificationSettingRepository userNotificationSettingRepository,
		NotificationTypeRepository notificationTypeRepository) {
		this.userRepository = userRepository;
		this.exhVisitRepository = exhVisitRepository;
		this.diaryRepository = diaryRepository;
		this.reasonRepository = reasonRepository;
		this.socialLoginRepository = socialLoginRepository;
		this.regExhRepository = regExhRepository;
		this.jwtUtil = jwtUtil;
		this.s3ImageTransfer = s3ImageTransfer;
		this.userNotificationSettingRepository = userNotificationSettingRepository;
		this.notificationTypeRepository = notificationTypeRepository;
	}

	@Override
	public FindUserResult getUserInfo() {
		UserEntity user = userRepository.findByUserId(getUserId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		return FindUserResult.findUserInfo(user);
	}

	@Override
	public String verifyNickname(VerifyNicknameQuery command) {
		Optional<UserEntity> user = userRepository.findByNickname(command.getNickname());

		if (user.isPresent()) {
			throw new ArtDiaryException(MessageType.CONFLICT);
		}
		return command.getNickname();
	}

	@Transactional
	@Override
	public FindUserResult loginTester(Long userId) {
		// noti
		List<NotiInfo> notiList = new ArrayList<>();
		List<Map<String, Object>> infoList = userNotificationSettingRepository.getUserNotificationSettingList(userId);
		UserEntity userEntity = userRepository.findByUserId(userId)
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		for (Map<String, Object> info : infoList) {
			NotificationTypeEntity notificationType = (NotificationTypeEntity)info.get("notificationType");
			UserNotificationSettingEntity userNotificationSetting = (UserNotificationSettingEntity)info.get(
				"userNotificationSetting");

			notiList.add(NotiInfo.builder()
				.notiId(notificationType.getNotiId())
				.notiCode(notificationType.getCode())
				.notiName(notificationType.getName())
				.notiSubText(notificationType.getSubText())
				.notiState(userNotificationSetting.getState())
				.build());
		}
		TokenInfo tokenInfo = jwtUtil.generateToken(userEntity.getUserId(), null);

		userEntity.updateRefreshToken(tokenInfo.getRefreshToken());
		return FindUserResult.findUserLoginInfo(userEntity, true, tokenInfo.getAccessToken(), notiList);
	}

	@Transactional
	@Override
	public FindUserResult socialLogin(Boolean forCheckEmail, Boolean wantUnite, UserCreateCommand command) {
		// 재로그인 확인
		Optional<SocialLoginEntity> socialLoginEntity = socialLoginRepository.findBySocialLoginId(
			SocialLoginId.builder()
				.providerType(command.getProviderType())
				.providerUserId(command.getProviderId()).build());
		UserEntity userEntity;
		List<NotiInfo> notiList = new ArrayList<>();

		if (socialLoginEntity.isPresent()) {
			// re
			// update provider type
			// get noti info
			userEntity = userRepository.findByUserId(socialLoginEntity.get().getUserId())
				.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
			userEntity.updateUser(UserEntity.builder()
				.providerType(command.getProviderType())
				.alarmToken(command.getAlarmToken())
				.build());
			userRepository.save(userEntity);
			List<Map<String, Object>> infoList = userNotificationSettingRepository.getUserNotificationSettingList(
				userEntity.getUserId());

			for (Map<String, Object> info : infoList) {
				NotificationTypeEntity notificationType = (NotificationTypeEntity)info.get("notificationType");
				UserNotificationSettingEntity userNotificationSetting = (UserNotificationSettingEntity)info.get(
					"userNotificationSetting");

				notiList.add(NotiInfo.builder()
					.notiId(notificationType.getNotiId())
					.notiCode(notificationType.getCode())
					.notiName(notificationType.getName())
					.notiSubText(notificationType.getSubText())
					.notiState(userNotificationSetting.getState())
					.build());
			}
		} else {
			// init
			// insert noti info
			userEntity = initLogin(forCheckEmail, wantUnite, command);
			insertSocialLogin(command, userEntity);
			List<NotificationTypeEntity> typeEntityList = notificationTypeRepository.findAll();

			for (NotificationTypeEntity entity : typeEntityList) {
				userNotificationSettingRepository.save(
					UserNotificationSettingEntity.builder().userNotificationSettingId(
						UserNotificationSettingId.builder()
							.userId(userEntity.getUserId())
							.notiId(entity.getNotiId())
							.build()).state(true).build());
				notiList.add(NotiInfo.builder()
					.notiId(entity.getNotiId())
					.notiCode(entity.getCode())
					.notiName(entity.getName())
					.notiSubText(entity.getSubText())
					.notiState(true)
					.build());
			}
		}
		TokenInfo tokenInfo = jwtUtil.generateToken(userEntity.getUserId(), null);
		userEntity.updateRefreshToken(tokenInfo.getRefreshToken());

		Boolean finishInit = !Objects.equals(userEntity.getNickname(),
			command.getProviderType() + "_" + command.getProviderId());

		return FindUserResult.findUserLoginInfo(userEntity, finishInit, tokenInfo.getAccessToken(), notiList);
	}

	private UserEntity initLogin(Boolean forCheckEmail, Boolean wantUnite, UserCreateCommand command) {
		// 초기 로그인
		if (forCheckEmail) {
			Boolean isExistedEmail = userRepository.existsByEmail(command.getEmail());

			if (isExistedEmail) {
				// 409 에러
				throw new ArtDiaryException(MessageType.CONFLICT);
			}
		}

		UserEntity userEntity;

		if (wantUnite) {
			userEntity = userRepository.findByEmail(command.getEmail())
				.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		} else {
			// 새로운 계정 생성
			userEntity = insertUser(command);
		}
		return userEntity;
	}

	@Override
	@Transactional
	public FindUserResult updateUser(UserUpdateCommand command) {
		UserEntity savedEntity = userRepository.findByUserId(getUserId()).orElseThrow(() -> new ArtDiaryException(
			MessageType.NOT_FOUND));
		// 닉네임 중복 확인
		if (!Objects.equals(savedEntity.getNickname(), command.getNickname())) {
			Optional<UserEntity> checkEntity = userRepository.findByNickname(command.getNickname());

			if (checkEntity.isPresent()) {
				throw new ArtDiaryException(MessageType.CONFLICT);
			}
		}
		// 사용자 사진 업데이트
		String uploadImageUrl = savedEntity.getProfile();
		if (command.getProfile() == null || !Objects.equals(command.getProfile().getOriginalFilename(),
			savedEntity.getProfile())) {
			uploadImageUrl = s3ImageTransfer.uploadImageToStorage(
				S3ImageTransfer.UploadQuery.builder()
					.type(ImageType.PROFILE)
					.image(command.getProfile())
					.prevImagePath(savedEntity.getProfile())
					.build());
		}
		// 사용자 정보 업데이트
		savedEntity.updateUser(UserEntity.builder()
			.nickname(command.getNickname())
			.profile(uploadImageUrl)
			.artField(command.getFavoriteArt())
			.build());
		userRepository.save(savedEntity);
		return FindUserResult.findUserInfo(savedEntity);
	}

	@Override
	@Transactional
	public void updateAlarm(UserAlarmUpdateCommand command) {
		Long userId = getUserId();
		UserNotificationSettingEntity notificationSetting = userNotificationSettingRepository.findByUserNotificationSettingId(
			UserNotificationSettingId.builder()
				.userId(userId)
				.notiId(command.getNotiId())
				.build()).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		notificationSetting.updateState(command.getSetNoti());
		userNotificationSettingRepository.save(notificationSetting);
	}

	@Override
	@Transactional
	public void deleteUser(DeleteReasonCommand command) {
		// - ExhVisit의 writerId와 Diary의 userId 값을 null로 변경
		List<ExhVisitEntity> exhVisitList = exhVisitRepository.findByUserId(getUserId());
		List<DiaryEntity> diaryList = diaryRepository.findByWriterId(getUserId());
		List<RegExhEntity> regExhList = regExhRepository.findByUserId(getUserId());

		for (ExhVisitEntity exhVisit : exhVisitList) {
			exhVisit.updateUserIdNull();
			exhVisitRepository.save(exhVisit);
		}
		for (DiaryEntity diary : diaryList) {
			diary.updateWriterIdNull();
			diaryRepository.save(diary);
		}
		for (RegExhEntity regExh : regExhList) {
			regExh.updateUserIdNull();
			regExhRepository.save(regExh);
		}

		// - 탈퇴 이유 reason에 저장.
		ReasonEntity reason = ReasonEntity.builder().reason(command.getReason()).build();
		reasonRepository.save(reason);

		// - user 테이블에서 사용자 삭제
		userRepository.deleteById(getUserId());
	}

	@Override
	@Transactional
	public void setAlarmToken(AlarmTokenUpdateCommand command) {
		UserEntity savedEntity = userRepository.findByUserId(getUserId()).orElseThrow(() -> new ArtDiaryException(
			MessageType.NOT_FOUND));

		savedEntity.updateUser(UserEntity.builder().alarmToken(command.getAlarmToken()).build());
	}

	@Override
	public FindAccessTokenResult reissueAccessToken(ReissueAccessTokenQuery command) {
		// access token 검증
		if (!jwtUtil.validateToken(command.getAccessToken(), true)) {
			throw new ArtDiaryException(MessageType.ReLogin);
		}
		// refresh token 검증
		Long userId = jwtUtil.getUserId(command.getAccessToken());
		UserEntity userEntity = userRepository.findByUserId(userId)
			.orElseThrow(() -> new ArtDiaryException(MessageType.ReLogin));

		if (!jwtUtil.validateToken(userEntity.getRefreshToken(), false)) {
			throw new ArtDiaryException(MessageType.ReLogin);
		}
		// access token 발급
		TokenInfo tokenInfo = jwtUtil.generateToken(userEntity.getUserId(), true);

		return FindAccessTokenResult.findAccessToken(tokenInfo.getAccessToken());
	}

	private Long getUserId() {
		return getCurrentUserEntity().getUserId();
	}

	private UserEntity insertUser(UserCreateCommand command) {
		// 닉네임 랜덤 생성
		String nickname = "오리";

		while (true) {
			// 	랜덤 숫자 생성
			Random random = new Random();
			int randomNumber = 100 + random.nextInt(900); // 100 ~ 999
			Optional<UserEntity> user = userRepository.findByNickname(nickname + randomNumber);

			if (user.isEmpty()) {
				nickname += randomNumber;
				break;
			}
		}
		UserEntity newUser = UserEntity.builder()
			.email(command.getEmail())
			.nickname(nickname)
			.profile(null)
			.artField(null)
			.alarmToken(command.getAlarmToken())
			.providerType(command.getProviderType())
			.roleType(RoleType.USER.label())
			.build();
		userRepository.save(newUser);
		return newUser;
	}

	private void insertSocialLogin(UserCreateCommand command, UserEntity user) {
		SocialLoginEntity newSocialLogin = SocialLoginEntity.builder()
			.socialLoginId(SocialLoginId.builder()
				.providerType(command.getProviderType())
				.providerUserId(command.getProviderId())
				.build())
			.userId(user.getUserId())
			.build();
		socialLoginRepository.save(newSocialLogin);
	}
}

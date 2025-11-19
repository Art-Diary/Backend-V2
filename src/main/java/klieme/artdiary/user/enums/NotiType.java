package klieme.artdiary.user.enums;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum NotiType {
	LIKE_EXH_NOTI(6L),
	SOLO_VISIT_NOTI(7L),
	GATHERING_VISIT_NOTI(8L),
	NEW_GATHERING_NOTI(9L),
	INVITED_GATHERING_NOTI(10L);

	private final Long label;

	NotiType(Long label) {
		this.label = label;
	}

	public Long label() {
		return this.label;
	}

	private static final Map<Long, NotiType> BY_LABEL = Stream.of(values())
		.collect(Collectors.toMap(NotiType::label, Function.identity()));

	public static NotiType valueOfLabel(Long label) {
		return BY_LABEL.get(label);
	}
}

package klieme.artdiary.exhibition.data_access.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.entity.QCategoryEntity;
import klieme.artdiary.exhibition.data_access.entity.QExhCategoryLinkEntity;
import klieme.artdiary.exhibition.data_access.entity.QExhEntity;
import klieme.artdiary.exhibition.enums.ExhField;
import klieme.artdiary.exhibition.enums.ExhPrice;
import klieme.artdiary.exhibition.enums.ExhState;
import klieme.artdiary.like_exh.data_access.entity.QLikeExhEntity;
import klieme.artdiary.record_data_access.entity.QVisitExhEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExhRepoCustomImpl implements ExhRepoCustom {
	private final JPAQueryFactory query;

	@Override
	public List<Map<String, Object>> searchExhList(String keyword, List<ExhField> fieldList, ExhPrice price,
		List<ExhState> stateList, LocalDate date, Long userId) {
		QExhEntity exh = QExhEntity.exhEntity;
		QLikeExhEntity likeExh = QLikeExhEntity.likeExhEntity;
		QCategoryEntity category = QCategoryEntity.categoryEntity;
		QExhCategoryLinkEntity exhCategoryLinkEntity = QExhCategoryLinkEntity.exhCategoryLinkEntity;
		BooleanBuilder builder = new BooleanBuilder();

		// 1) 아무 필터도 없으면 '현재 진행 중'만 보여주기 (기본조건)
		if (noFilters(fieldList, price, stateList, date)) { //아무 조건도 없을 때
			LocalDate now = LocalDate.now();

			builder.and(exh.startDate.loe(now)); // start <= now
			builder.and(exh.endDate.goe(now)); // end >= now
		} else {
			// 2) 개별 필터 조합
			builder.and(buildFieldPredicate(fieldList, category));
			builder.and(buildPricePredicate(price, exh));
			builder.and(buildStatePredicate(stateList, date, exh));
		}

		List<Tuple> tuples = query.select(exh, likeExists(likeExh, exh, userId))
			.distinct()
			.from(exh)
			.leftJoin(likeExh).on(exh.exhId.eq(likeExh.likeExhId.exhId))
			.leftJoin(exhCategoryLinkEntity).on(exh.exhId.eq(exhCategoryLinkEntity.exhCategoryLinkId.exhId))
			.leftJoin(category).on(exhCategoryLinkEntity.exhCategoryLinkId.categoryId.eq(category.categoryId))
			.fetchJoin()
			.where(builder)
			.groupBy(exh.exhId)
			.orderBy(likeExh.likeExhId.exhId.count().desc(), exh.exhName.asc())
			.fetch();

		List<Map<String, Object>> result = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("exhibition", tuple.get(0, ExhEntity.class));
			row.put("isLikeExh", tuple.get(1, Boolean.class));
			result.add(row);
		}
		return result;
	}

	@Override
	public List<ExhEntity> getNotVisitedExhListWithDate(LocalDate date, Long userId) {
		QExhEntity exh = QExhEntity.exhEntity;
		QVisitExhEntity visitExh = QVisitExhEntity.visitExhEntity;

		return query.select(exh)
			.from(exh)
			.where(exh.startDate.loe(date), exh.endDate.goe(date), exh.exhId.notIn(query.select(visitExh.exhId)
				.from(visitExh)
				.where(visitExh.visitDate.eq(date), visitExh.userId.eq(userId))))
			.fetch();
	}

	@Override
	public List<ExhEntity> getNotVisitedExhListWithDateInGathering(LocalDate date, Long gatheringId) {
		QExhEntity exh = QExhEntity.exhEntity;
		QVisitExhEntity visitExh = QVisitExhEntity.visitExhEntity;

		return query.select(exh)
			.from(exh)
			.where(exh.startDate.loe(date), exh.endDate.goe(date), exh.exhId.notIn(query.select(visitExh.exhId)
				.from(visitExh)
				.where(visitExh.visitDate.eq(date), visitExh.gatheringId.eq(gatheringId))))
			.fetch();
	}

	@Override
	public List<Map<String, Object>> searchExhListBySearchName(String searchName, Long userId) {

		QExhEntity exh = QExhEntity.exhEntity;
		QLikeExhEntity likeExh = QLikeExhEntity.likeExhEntity;
		BooleanBuilder builder = new BooleanBuilder();

		if (searchName != null) {
			builder.and(exh.exhName.containsIgnoreCase(searchName)
				.or(exh.gallery.containsIgnoreCase(searchName))
				.or(exh.painter.containsIgnoreCase(searchName)));
		}

		OrderSpecifier<Integer> priorityOrder = new CaseBuilder()
			.when(exh.exhName.containsIgnoreCase(searchName)).then(1) // 1순위: exhName
			.when(exh.gallery.containsIgnoreCase(searchName)).then(2) // 2순위: gallery
			.when(exh.painter.containsIgnoreCase(searchName)).then(3) // 3순위: painter
			.otherwise(4) // 나머지
			.asc();

		// 필드별 정렬 조건
		OrderSpecifier<String> exhNameOrder = new CaseBuilder()
			.when(exh.exhName.containsIgnoreCase(searchName)).then(exh.exhName)
			.otherwise((String)null) // 다른 조건일 때는 무시
			.asc();

		OrderSpecifier<String> galleryOrder = new CaseBuilder()
			.when(exh.gallery.containsIgnoreCase(searchName)).then(exh.gallery)
			.otherwise((String)null)
			.asc();

		OrderSpecifier<String> painterOrder = new CaseBuilder()
			.when(exh.painter.containsIgnoreCase(searchName)).then(exh.painter)
			.otherwise((String)null)
			.asc();

		List<Tuple> tuples = query.select(exh, likeExists(likeExh, exh, userId))
			.distinct()
			.from(exh)
			.where(builder)
			.orderBy(priorityOrder, exhNameOrder, galleryOrder, painterOrder)
			.fetch();

		List<Map<String, Object>> result = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("exhibition", tuple.get(0, ExhEntity.class));
			row.put("haveFavoriteByUser", tuple.get(1, Boolean.class));
			result.add(row);
		}
		return result;

	}

	@Override
	public List<Map<String, Object>> getExhListForExhData() {
		QExhEntity exh = QExhEntity.exhEntity;
		QCategoryEntity category = QCategoryEntity.categoryEntity;
		QExhCategoryLinkEntity exhCategoryLinkEntity = QExhCategoryLinkEntity.exhCategoryLinkEntity;

		List<Tuple> tuples = query.select(exh, Expressions.stringTemplate("GROUP_CONCAT({0})", category.name))
			.distinct()
			.from(exh)
			.leftJoin(exhCategoryLinkEntity).on(exh.exhId.eq(exhCategoryLinkEntity.exhCategoryLinkId.exhId))
			.leftJoin(category).on(exhCategoryLinkEntity.exhCategoryLinkId.categoryId.eq(category.categoryId))
			.fetchJoin()
			.groupBy(exh.exhId)
			.orderBy(exh.exhId.desc())
			.fetch();
		List<Map<String, Object>> result = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("exhibition", tuple.get(0, ExhEntity.class));
			row.put("category", tuple.get(1, String.class));
			result.add(row);
		}
		return result;
	}

	@Override
	public Map<String, Object> getExhDetailInfoWithIsLike(Long userId, Long exhId) {
		QExhEntity exh = QExhEntity.exhEntity;
		QLikeExhEntity likeExh = QLikeExhEntity.likeExhEntity;

		Tuple tuple = query.select(exh, likeExists(likeExh, exh, userId))
			.from(exh)
			.where(exh.exhId.eq(exhId))
			.fetchOne();

		if (tuple == null) {
			return null;
		}
		Map<String, Object> result = new HashMap<>();

		result.put("exhibition", tuple.get(0, ExhEntity.class));
		result.put("isLikeExh", tuple.get(1, Boolean.class));
		return result;
	}

	private BooleanExpression likeExists(QLikeExhEntity likeExh, QExhEntity exh, Long userId) {
		// 유저가 ‘좋아요’ 했는지 여부를 boolean으로 바로 select
		return JPAExpressions
			.selectOne()
			.from(likeExh)
			.where(likeExh.likeExhId.exhId.eq(exh.exhId).and(likeExh.likeExhId.userId.eq(userId)))
			.exists();
	}

	private BooleanBuilder buildFieldPredicate(List<ExhField> fieldList, QCategoryEntity category) {
		if (fieldList == null || fieldList.isEmpty())
			return new BooleanBuilder();

		BooleanBuilder orBuilder = new BooleanBuilder();

		for (ExhField field : fieldList) {
			if (field == ExhField.OTHER) {
				// 카테고리 연결이 없는 전시
				orBuilder.or(category.name.isNull());
			} else {
				orBuilder.or(category.name.eq(field.label()));
			}
		}
		return new BooleanBuilder().and(orBuilder);
	}

	private BooleanBuilder buildPricePredicate(ExhPrice price, QExhEntity exh) {
		if (price == null)
			return new BooleanBuilder();

		if (price == ExhPrice.FREE) {
			return new BooleanBuilder().and(exh.fee.eq(0));
		} else if (price == ExhPrice.PAY) {
			return new BooleanBuilder().and(exh.fee.ne(0));
		} else {
			// 예: 저가(2만원 이하)
			return new BooleanBuilder().and(exh.fee.loe(20000));
		}
	}

	private BooleanBuilder buildStatePredicate(List<ExhState> stateList, LocalDate date, QExhEntity exh) {
		// date가 있으면 상태 무시하고 그 날짜에 진행 중인 전시만
		LocalDate now = (date != null) ? date : LocalDate.now();

		if (date != null) {
			return new BooleanBuilder()
				.and(exh.startDate.loe(now)) // start <= now
				.and(exh.endDate.goe(now)); // end >= now
		}

		if (stateList == null || stateList.isEmpty())
			return new BooleanBuilder();

		BooleanBuilder orBuilder = new BooleanBuilder();
		for (ExhState state : stateList) {
			switch (state) {
				case PROCEED:
					orBuilder.or(
						new BooleanBuilder()
							.and(exh.startDate.loe(now)) // start <= now
							.and(exh.endDate.goe(now)) // end >= now
					);
					break;
				case BEFORE_START:
					orBuilder.or(exh.startDate.gt(now)); // start > now
					break;
				case END:
				default:
					orBuilder.or(exh.endDate.lt(now)); // end < now
					break;
			}
		}
		return new BooleanBuilder().and(orBuilder);
	}

	private boolean noFilters(List<ExhField> fieldList, ExhPrice price, List<ExhState> stateList, LocalDate date) {
		return (fieldList == null || fieldList.isEmpty())
			&& price == null
			&& (stateList == null || stateList.isEmpty())
			&& date == null;
	}
}

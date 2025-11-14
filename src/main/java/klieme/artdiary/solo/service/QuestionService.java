package klieme.artdiary.solo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import klieme.artdiary.solo.data_access.entity.QuestionEntity;
import klieme.artdiary.solo.data_access.repository.QuestionRepository;

@Service
public class QuestionService implements QuestionReadUseCase {
	private final QuestionRepository questionRepository;

	@Autowired
	public QuestionService(QuestionRepository questionRepository) {
		this.questionRepository = questionRepository;
	}

	@Override
	public List<FindQuestionResult> getQuestionList() {
		List<QuestionEntity> questionEntityList = questionRepository.findAll();
		List<FindQuestionResult> result = new ArrayList<>();

		for (QuestionEntity entity : questionEntityList) {
			result.add(FindQuestionResult.of(entity));
		}
		return result;
	}
}

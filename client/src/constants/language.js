import { MAX_AMOUNT_OF_WORDS } from '../utils/queryRules';

export const RUSSIAN_LANGUAGE = 'RU';
export const ENGLISH_LANGUAGE = 'EN';

export const loaderText = {
  RU: 'Поиск',
  EN: 'Searching',
}

export const searchInputPlaceholder = {
  RU: 'Найти в новостях...',
  EN: 'Search in news...',
};

export const searchButtonText = {
  RU: 'ПОИСК',
  EN: 'SEARCH',
};

export const lunguageButtonText = {
  RU: 'РУССКИЙ',
  EN: 'ENGLISH',
};

export const tabText = {
  RU: 'ТЕКСТ',
  EN: 'TEXT',
};

export const tabImage = {
  RU: 'Картинки',
  EN: 'Images',
};

export const aboutResultsTextFirstPart = {
  RU: 'Около',
  EN: 'About',
};

export const aboutResultsTextSecondPart = {
  RU: 'результатов',
  EN: 'results',
};

export const similarResultsText = {
  RU: 'Похожие поисковые запросы',
  EN: 'Related searches',
};

export const resultErrorFirstPart = {
  RU: 'Ваш поиск',
  EN: 'Your search',
};

export const resultNotFoundSecondPart = {
  RU: 'не соответствует ни одному документу.',
  EN: 'did not match any documents.',
};

export const suggestionsTitle = {
  RU: 'Предложения:',
  EN: 'Suggestions:',
};

export const warningWrongSearchQuery = {
  RU: 'Убедитесь, что все слова написаны правильно',
  EN: 'Make sure that all words are spelled correctly',
};

export const warningWrongSearchQuerySuggestion1 = {
  RU: 'Попробуйте разные ключевые слова',
  EN: 'Try different keywords',
};

export const warningWrongSearchQuerySuggestion2 = {
  RU: 'Попробуйте использовать более общие ключевые слова',
  EN: 'Try more general keywords',
};

export const warningWrongSearchQuerySuggestion3 = {
  RU: 'Попробуйте использовать меньшее количество ключевых слов',
  EN: 'Try fewer keywords',
};

export const resultErrorSecondPart = {
  RU: 'нарушает следующие правила запроса:',
  EN: 'breaks the rules:',
};

export const searchQueryRulesTitle = {
  RU: 'Правила поискового запроса:',
  EN: 'Search query rules:',
};

export const rule1 = {
  RU: `Максимальное количество слов - ${MAX_AMOUNT_OF_WORDS}`,
  EN: `Maximum number of words - ${MAX_AMOUNT_OF_WORDS}`,
};

export const rule2 = {
  RU: 'Содержит английские или русские символы, числа или знаки препинания',
  EN: 'Contains English or Russian characters, numbers or punctuation marks',
};

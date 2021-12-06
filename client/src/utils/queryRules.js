import { rule1, rule2 } from '../constants/language';

const localeRegex = /^[-_=*+а-яА-ЯёЁa-zA-Z0-9!?,.;:()№%'"\s]+$/;
export const MAX_AMOUNT_OF_WORDS = 16;

export const getBreaksRules = (query) => {
  const isBreakLocale = !localeRegex.test(query);
  const isBreakAmountOfWords = query.trim().replace(/\s\s+/g, ' ').split(' ').length > MAX_AMOUNT_OF_WORDS;

  const breaksRules = [];

  if (isBreakAmountOfWords) {
    breaksRules.push(rule1);
  }
  if (isBreakLocale) {
    breaksRules.push(rule2);
  }

  return breaksRules;
};

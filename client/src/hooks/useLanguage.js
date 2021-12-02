import { useCallback, useState } from 'react';
import { getFromLocalStorage, setToLocalStorage } from '../utils/localStorage';
import { RUSSIAN_LANGUAGE } from '../constants/language';

const LOCAL_STORAGE_KEY = 'news-searcher-language';

export const useLanguage = () => {
  const [language, setLanguage] = useState(getFromLocalStorage(LOCAL_STORAGE_KEY) || RUSSIAN_LANGUAGE);

  const changeLanguage = useCallback((language = RUSSIAN_LANGUAGE) => {
    setLanguage(language);
    setToLocalStorage(LOCAL_STORAGE_KEY, language);
  }, [setLanguage]);

  return [language, changeLanguage];
};

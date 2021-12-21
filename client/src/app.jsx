import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './styles/index.scss';

import UiKit from './pages/ui-kit';
import Welcome from './pages/welcome';
import Search from './pages/search';
import NotFoundPage from './pages/notFoundPage';
import { SEARCH_PAGE_PATH, UI_KIT_PAGE_PATH, WELCOME_PAGE_PATH } from './constants/url';

function App() {

  return (
    <Router>
      <Routes>
        <Route path={UI_KIT_PAGE_PATH} element={<UiKit />} />
        <Route path={WELCOME_PAGE_PATH} element={<Welcome />} />
        <Route path={SEARCH_PAGE_PATH} element={<Search />} />
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </Router>
  );
}

export default App;

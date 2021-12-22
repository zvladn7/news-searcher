import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './styles/index.scss';

import Welcome from './pages/welcome';
import Search from './pages/search';
import NotFoundPage from './pages/notFoundPage';
import { SEARCH_PAGE_PATH, WELCOME_PAGE_PATH } from './constants/url';

function App() {

  return (
    <Router>
      <Routes>
        <Route path={WELCOME_PAGE_PATH} element={<Welcome />} />
        <Route path={SEARCH_PAGE_PATH} element={<Search />} />
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </Router>
  );
}

export default App;

import React from 'react';
import { BrowserRouter, Switch, Route } from 'react-router-dom';
import './styles/index.scss';

import UiKit from './pages/ui-kit';

function App() {
  return (
    <BrowserRouter>
      <Switch>
        <Route path="/ui-kit">
          <UiKit />
        </Route>
        <Route path="/">
          <div>Main</div>
        </Route>
      </Switch>
    </BrowserRouter>
  );
}

export default App;

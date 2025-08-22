import { BrowserRouter, Route, Routes } from "react-router-dom";
import LoginPage from "./components/auth/LoginPage";
import MyUserReducer from "./components/reducers/MyUserReducer";
import { useReducer } from "react";
import { MyUserContext } from "./components/Context/MyContext";
import Home from "./components/Job/Home";
import CandidateRegisterPage from "./components/auth/CandidateRegisterPage";
import EmployerRegisterPage from "./components/auth/EmployerRegisterPage";

function App() {
  const [user, dispatch] = useReducer(MyUserReducer, null);

  return (
    <>
      <MyUserContext.Provider value={[user, dispatch]}>
        <BrowserRouter>
          <Routes>
            {user ? (
              <>
                {user.result && user.result.role === "CANDIDATE" && (
                  <>
                    <Route path='/' element={<Home />} />
                  </>
                )}
              </>
            ) : (
              <>
                <Route path='/' element={<Home />} />
                <Route path='/login' element={<LoginPage />} />
                <Route
                  path='/candidate-register'
                  element={<CandidateRegisterPage />}
                />
                <Route
                  path='/employer-register'
                  element={<EmployerRegisterPage />}
                />
              </>
            )}
          </Routes>
        </BrowserRouter>
      </MyUserContext.Provider>
    </>
  );
}

export default App;

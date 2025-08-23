import { BrowserRouter, Route, Routes } from "react-router-dom";
import LoginPage from "./components/auth/LoginPage";
import MyUserReducer from "./components/reducers/MyUserReducer";
import { useReducer } from "react";
import { MyUserContext } from "./components/Context/MyContext";
import Home from "./components/Job/Home";
import CandidateRegisterPage from "./components/auth/CandidateRegisterPage";
import EmployerRegisterPage from "./components/auth/EmployerRegisterPage";
import RegisterSwitch from "./components/auth/RegisterSwitch";
import JobDetail from "./components/Job/JobDetail";
import ApplicationTracking from "./components/Job/ApplicationTracking";

function App() {
  const [user, dispatch] = useReducer(MyUserReducer, null);

  return (
    <>
      <MyUserContext.Provider value={[user, dispatch]}>
        <BrowserRouter>
          <Routes>
            {user ? (
              <>
                {user && user.role === "CANDIDATE" && (
                  <>
                    <Route path='/' element={<Home />} />
                    <Route
                      path='/application-tracking'
                      element={<ApplicationTracking />}
                    />
                    <Route path='/job-detail/:id' element={<JobDetail />} />
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
                <Route path='/register-switch' element={<RegisterSwitch />} />
                <Route path='/job-detail/:id' element={<JobDetail />} />
              </>
            )}
          </Routes>
        </BrowserRouter>
      </MyUserContext.Provider>
    </>
  );
}

export default App;

import { BrowserRouter, Route, Routes } from "react-router-dom";
import LoginPage from "./components/auth/LoginPage";
import MyUserReducer from "./components/reducers/MyUserReducer";
import { useEffect, useReducer } from "react";
import { MyUserContext } from "./components/Context/MyContext";
import Home from "./components/Job/Home";
import CandidateRegisterPage from "./components/auth/CandidateRegisterPage";
import EmployerRegisterPage from "./components/auth/EmployerRegisterPage";
import RegisterSwitch from "./components/auth/RegisterSwitch";
import JobDetail from "./components/Job/JobDetail";
import ApplicationTracking from "./components/Job/ApplicationTracking";
import { authApis, endpoints } from "./configs/Apis";
import cookies from "react-cookies";
import CreateJob from "./components/Job/CreateJob";
import JobTracking from "./components/Job/JobTracking";
import ApproveJob from "./components/Employer/ApproveJob";
import CandidateApply from "./components/Employer/CandidateApply";

function App() {
  const [user, dispatch] = useReducer(MyUserReducer, null);

  const getUserFromToken = async () => {
    const token = cookies.load("token");

    if (token) {
      try {
        let user = await authApis(token).get(endpoints.user);
        console.info(user.data);
        dispatch({ type: "login", payload: user.data });
      } catch (error) {
        console.error("Failed to fetch user profile with stored token:", error);
        dispatch({ type: "logout" });
      }
    }
  };
  useEffect(() => {
    getUserFromToken();
  }, []);

  console.log("Current user:", user?.role);

  return (
    <>
      <MyUserContext.Provider value={[user, dispatch]}>
        <BrowserRouter>
          <Routes>
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
            {user ? (
              user.role === "CANDIDATE" ? (
                <>
                  <Route path='/' element={<Home />} />
                  <Route
                    path='/application-tracking'
                    element={<ApplicationTracking />}
                  />
                  <Route path='/job-detail/:id' element={<JobDetail />} />
                </>
              ) : user.role === "EMPLOYER" ? (
                <>
                  <Route path='/create-job' element={<CreateJob />} />
                  <Route path='/job-tracking' element={<JobTracking />} />
                  <Route path='/approve-job' element={<ApproveJob />} />
                  <Route
                    path='/employer/job/:jobId/candidates'
                    element={<CandidateApply />}
                  />
                </>
              ) : null
            ) : (
              <></>
            )}
          </Routes>
        </BrowserRouter>
      </MyUserContext.Provider>
    </>
  );
}

export default App;

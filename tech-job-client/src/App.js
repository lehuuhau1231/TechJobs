import { BrowserRouter, Route, Routes } from "react-router-dom";
import LoginPage from "./components/auth/LoginPage";
import MyUserReducer from "./components/reducers/MyUserReducer";
import { useReducer } from "react";
import { MyUserContext } from "./components/Context/MyContext";
import Home from "./components/Job/Home";

function App() {
  const [user, dispatch] = useReducer(MyUserReducer, null);

  return (
    <>
      <MyUserContext.Provider value={{ user, dispatch }}>
        <BrowserRouter>
          <Routes>
            {user ? (
              <>
                {user.role === "CANDIDATE" && (
                  <>
                    <Route path='/' element={<Home />} />
                  </>
                )}
              </>
            ) : (
              <Route path='/' element={<Home />} />
            )}
          </Routes>
        </BrowserRouter>
      </MyUserContext.Provider>
    </>
  );
}

export default App;

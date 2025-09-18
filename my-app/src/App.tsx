import Header from "./components/Header";
import { Outlet, useNavigation } from "react-router-dom";

function App() {
  const navigation = useNavigation();
  return (
    <>
      <Header />
      {navigation.state === "loading" ? (
        <div className="flex items-center justify-center min-h-[852px]">
          <span className="text-4xl font-semibold ">Loading....</span>
        </div>
      ) : (
        <Outlet />
      )}
    </>
  );
}

export default App;

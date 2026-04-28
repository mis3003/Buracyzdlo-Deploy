import { useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";

export default function SpotifySuccessPage() {
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const code = params.get("code");

    if (!code) {
      navigate("/error");
      return;
    }

    const callBackend = async () => {
      try {
        const res = await fetch(`http://localhost:8080/api/spotify/callback?code=${code}`, {
          method: "GET",
          credentials: "include",
        });
const text = await res.text();
console.log("Status:", res.status);
console.log("Body:", text);
        if (res.ok) {
          console.log("Zalogowano pomyślniasfnlkahsbgflkjashnflkhaslfjasklfhlkj");
          navigate("/app/playlist");
        } else {
          navigate("/error");
        }
      } catch (err) {
        console.error("Błąd:", err);
        navigate("/error");
      }
    };

    callBackend();
  }, [location.search, navigate]);

  return <div>Logowanie przez Spotify...</div>;
}

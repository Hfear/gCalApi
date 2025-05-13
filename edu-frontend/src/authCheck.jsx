import { redirect } from 'react-router-dom';

export async function authCheck() {

  const res = await fetch('/oauth/isAuth', {
    credentials: 'include'
  });
  if (!res.ok) {
    throw redirect('/signin');
  }
  const ok = await res.json();
  if (!ok) {
    // not signed in
    throw redirect('/signin');
  }
}

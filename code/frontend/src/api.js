// Shared API helpers. Base URL matches the existing Auth component.
export const API = 'http://localhost:8080'

// Optional handler invoked when the session is no longer valid (401/403),
// e.g. an expired JWT. App registers this to route the user back to login.
let authErrorHandler = null
export function setAuthErrorHandler(fn) {
  authErrorHandler = fn
}

// Authenticated fetch: attaches the stored JWT and parses JSON (204 -> null).
export async function authFetch(path, options = {}) {
  const token = localStorage.getItem('token')
  const res = await fetch(`${API}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(options.headers || {}),
    },
  })

  let data = null
  if (res.status !== 204) {
    try {
      data = await res.json()
    } catch {
      data = null
    }
  }

  if (!res.ok) {
    // An expired or missing JWT surfaces as 401/403. Clear the stale session
    // and let the app send the user back to the login screen.
    if (res.status === 401 || res.status === 403) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      if (authErrorHandler) authErrorHandler()
      throw new Error('Your session has expired. Please log in again.')
    }
    const message = (data && data.message) || `Request failed (${res.status})`
    throw new Error(message)
  }
  return data
}

export const inr = (n) => `₹${Number(n || 0).toLocaleString('en-IN')}`

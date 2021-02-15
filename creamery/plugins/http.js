export default function (context, _inject) {
  // handler function that will be executed before each and every remote request
  context.$http.onRequest((httpConfig) => {
    // try to get JTW token from local storage
    const authJwt = context.store.state.localStorage.authJwt
    if (authJwt) {
      // set JWT as a bearer token into authorization header,
      // if JWT was present in local storage
      httpConfig.headers.set('Authorization', 'Bearer ' + authJwt)
    }
  })
  // handler function that will be executed if remote request resulted in error
  context.$http.onError((error) => {
    let errorMessageToShow = null
    let isLogError = true
    // if error related to auth then set standard auth error message,
    // otherwise try to get 'errorMessage' field from the response body,
    // otherwise try to get raw response body data as an error message,
    // otherwise set error message to standard client or server error messages
    if (error.statusCode === 401) {
      // standard error message on not authenticated
      errorMessageToShow = '$errorMessages.userNotAuthenticated'
      isLogError = false
    } else if (error.statusCode === 403) {
      // standard error message on not authorized
      errorMessageToShow = '$errorMessages.userNotAuthorized'
      isLogError = false
    } else if (
      error.response &&
      error.response.data &&
      error.response.data.errorMessage
    ) {
      errorMessageToShow = error.response.data.errorMessage
      isLogError = false
    } else if (error.response && error.response.data) {
      errorMessageToShow = error.response.data
    } else if (error.statusCode >= 400 && error.statusCode < 500) {
      errorMessageToShow = '$errorMessages.clientError'
    } else if (error.statusCode >= 500 && error.statusCode < 600) {
      errorMessageToShow = '$errorMessages.serverError'
    } else {
      // no status code is a server error
      errorMessageToShow = '$errorMessages.serverError'
    }

    // log error if error message was not present in 'errorMessage' field
    if (isLogError) {
      console.log(error)
    }
    // persist error message in local storage, to display it in popup
    context.store.commit('localStorage/setErrorMessage', errorMessageToShow)
  })
}

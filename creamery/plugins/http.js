export default function (context, _inject) {
  context.$http.onRequest((httpConfig) => {
    const authJwt = context.store.state.localStorage.authJwt
    if (authJwt) {
      httpConfig.headers.set('Authorization', 'Bearer ' + authJwt)
    }
  })
  context.$http.onError((error) => {
    let errorMessageToShow = null
    let isLogError = true
    if (!error.statusCode) {
      errorMessageToShow = '$errorMessages.serverError'
    } else if (error.statusCode === 400) {
      if (
        error.response &&
        error.response.data &&
        error.response.data.errorMessage
      ) {
        errorMessageToShow = error.response.data.errorMessage
        isLogError = false
      } else if (error.response && error.response.data) {
        errorMessageToShow = error.response.data
      } else {
        errorMessageToShow = '$errorMessages.clientError'
      }
    } else if (error.statusCode === 401) {
      errorMessageToShow = '$errorMessages.userNotAuthorized'
      isLogError = false
    } else if (error.statusCode === 403) {
      errorMessageToShow = '$errorMessages.userNotAuthenticated'
      isLogError = false
    } else if (error.statusCode >= 400 && error.statusCode < 500) {
      errorMessageToShow = '$errorMessages.clientError'
    } else if (error.statusCode >= 500 && error.statusCode < 600) {
      errorMessageToShow = '$errorMessages.serverError'
    } else {
      errorMessageToShow = '$errorMessages.serverError'
    }
    if (isLogError) {
      console.log(error)
    }
    // show message
    context.store.commit('localStorage/setErrorMessage', errorMessageToShow)
  })
}

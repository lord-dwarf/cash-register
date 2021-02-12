export default function (context, _inject) {
  context.$http.onRequest((httpConfig) => {
    const authJwt = context.app.store.state.localStorage.authJwt
    if (authJwt) {
      httpConfig.headers.set('Authorization', 'Bearer ' + authJwt)
    }
  })
  context.$http.onError((error) => {
    let errorMessageToShow = null
    let isLogError = true
    if (!error.statusCode) {
      errorMessageToShow = 'Service error'
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
        errorMessageToShow = 'Client error'
      }
    } else if (error.statusCode === 401) {
      errorMessageToShow = 'User not authorized'
      isLogError = false
    } else if (error.statusCode === 403) {
      errorMessageToShow = 'User not authenticated'
      isLogError = false
    } else if (error.statusCode >= 400 && error.statusCode < 500) {
      errorMessageToShow = 'Client error'
    } else if (error.statusCode >= 500 && error.statusCode < 600) {
      errorMessageToShow = 'Service error'
    } else {
      errorMessageToShow = 'Service error'
    }
    if (isLogError) {
      console.log(error)
    }
    // show message
    context.app.store.commit('localStorage/setErrorMessage', errorMessageToShow)
  })
}

export default function (context, _inject) {
  context.$http.onRequest((httpConfig) => {
    const authJwt = context.app.store.state.localStorage.authJwt
    if (authJwt) {
      httpConfig.headers.set('Authorization', 'Bearer ' + authJwt)
    }
  })
}

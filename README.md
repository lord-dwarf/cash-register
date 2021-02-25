# Cash Register Application
![Header Image](doc/cashregister.png)
# Contents
[Purpose](#purpose)<br>
[Functionality](#functionality)<br>
[Technologies](#technologies)<br>
[Author](#author)

TODO strip strips
TODO replace boiler plate code with lombok
TODO add rules for text fields on UI

///////////////////////////

    public void putJwtIntoResponse(String jwt, HttpServletRequest request, HttpServletResponse response) {
        Cookie jwtCookie = new Cookie(JWT_AUTH_COOKIE_NAME, jwt);
        jwtCookie.setMaxAge(jwtExpirationSeconds);
        jwtCookie.setPath(request.getContextPath());
        response.addCookie(jwtCookie);
    }

    public Optional<String> getJwtFromRequest(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (null == cookies) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter((cookie) -> JWT_AUTH_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(Objects::nonNull)
                .findFirst();
    }

    public JwtDto parseJwt(String jwt) {
        Jws<Claims> jwtParsed = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt);
        var body = jwtParsed.getBody();
        return new JwtDto()
                .setUserId(body.getSubject())
                .setRole(body.get(JWT_CLAIM_ROLE, List.class).get(0).toString())
                .setIssuedAt(CashRegisterUtil.toLocalDateTime(body.getIssuedAt()))
                .setExpireAt(CashRegisterUtil.toLocalDateTime(body.getExpiration()));
    }

# <a name="purpose"></a>Purpose
A cash register or till is a mechanical or electronic device for registering and calculating transactions at a point of sale. It is usually attached to a drawer for storing cash and other valuables. A modern cash register is usually attached to a printer that can print out receipts for record-keeping purposes.
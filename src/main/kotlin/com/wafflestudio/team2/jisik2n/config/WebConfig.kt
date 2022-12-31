package com.wafflestudio.team2.jisik2n.config

import com.wafflestudio.team2.jisik2n.common.Authenticated
import com.wafflestudio.team2.jisik2n.common.Jisik2n401
import com.wafflestudio.team2.jisik2n.common.UserContext
import com.wafflestudio.team2.jisik2n.core.user.database.TokenRepository
import com.wafflestudio.team2.jisik2n.core.user.service.AuthTokenService
import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.method.HandlerMethod
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
class WebConfig(
    private val authInterceptor: AuthInterceptor,
    private val authArgumentResolver: AuthArgumentResolver,
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(authInterceptor)
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(authArgumentResolver)
    }
}

@Configuration
class AuthArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(UserContext::class.java) &&
            parameter.parameterType == Long::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        parameter.hasMethodAnnotation(UserContext::class.java)
        return (webRequest as ServletWebRequest).request.getAttribute("userId")
    }
}

@Configuration
class AuthInterceptor(
    private val authTokenService: AuthTokenService,
    private val tokenRepository: TokenRepository
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val handlerCasted = (handler as? HandlerMethod) ?: return true
        if (handlerCasted.hasMethodAnnotation(Authenticated::class.java)) {

            val accessToken = request.getHeader("Authorization") ?: throw Jisik2n401("토큰 인증 적절하지 않아 accessToken 생성 실패")
            val refreshToken = request.getHeader("RefreshToken") ?: throw Jisik2n401("토큰 인증 적절하지 않아 refreshToken 생성 실패")

            if (authTokenService.verifyToken(refreshToken) != true) {
                throw Jisik2n401("refresh token이 적절하지 않습니다.")
            }
            if (authTokenService.verifyToken(accessToken) == true) { // access token 정상적 작동

                val userId = authTokenService.getCurrentUserId(accessToken)
                request.setAttribute("userId", userId)
            } else { // access token이 만료되었거나, 존재하지도 않거나

                // Bearer 제거
                val prefixRemoved = accessToken.replace("Bearer ", "").trim { it <= ' ' }
                val tokenEntity = tokenRepository.findByAccessToken(prefixRemoved)
                if (tokenEntity == null) { // access token이 존재하지 않음

                    throw Jisik2n401("access token이 적절하지 않습니다.")
                } else { // access token 만료됨

                    if (refreshToken.replace("Bearer ", "").trim { it <= ' ' } == tokenEntity.refreshToken) {
                        val newAccessToken = authTokenService.generateAccessTokenByUid(tokenEntity.keyUid)
                        tokenEntity.accessToken = newAccessToken
                        tokenRepository.save(tokenEntity)
                        val userId = authTokenService.getCurrentUserId(newAccessToken)
                        request.setAttribute("userId", userId)
                    } else {
                        throw Jisik2n401("refresh token이 적절하지 않습니다.")
                    }
                }
            }
        }

        return super.preHandle(request, response, handler)
    }
}

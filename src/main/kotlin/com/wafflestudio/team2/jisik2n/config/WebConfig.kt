package com.wafflestudio.team2.jisik2n.config

import com.wafflestudio.team2.jisik2n.common.Authenticated
import com.wafflestudio.team2.jisik2n.common.Jisik2n400
import com.wafflestudio.team2.jisik2n.common.Jisik2n401
import com.wafflestudio.team2.jisik2n.common.UserContext
import com.wafflestudio.team2.jisik2n.core.user.database.BlacklistTokenRepository
import com.wafflestudio.team2.jisik2n.core.user.database.TokenRepository
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import com.wafflestudio.team2.jisik2n.core.user.database.UserRepository
import com.wafflestudio.team2.jisik2n.core.user.service.AuthTokenService
import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.data.repository.findByIdOrNull
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
        println(parameter.parameterType)
        return parameter.hasParameterAnnotation(UserContext::class.java) &&
            parameter.parameterType == UserEntity::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        parameter.hasMethodAnnotation(UserContext::class.java)
        return (webRequest as ServletWebRequest).request.getAttribute("userEntity")
    }
}

@Configuration
class AuthInterceptor(
    private val authTokenService: AuthTokenService,
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository,
    private val blacklistTokenRepository: BlacklistTokenRepository,
) : HandlerInterceptor {

//    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
//        val handlerCasted = (handler as? HandlerMethod) ?: return true
//        if (handlerCasted.hasMethodAnnotation(Authenticated::class.java)) {
//            val accessToken = request.getHeader("Authorization") ?: throw Jisik2n401("access Token 획득 실패")
//            val refreshToken = request.getHeader("RefreshToken") ?: throw Jisik2n401("refresh Token 획득 실패")
//
//            val prefixRemovedAccessToken = accessToken.replace("Bearer ", "").trim { it <= ' ' }
//            val prefixRemovedRefreshToken = refreshToken.replace("Bearer ", "").trim { it <= ' ' }
//            if (blacklistTokenRepository.findByAccessToken(prefixRemovedAccessToken) != null) {
//                throw Jisik2n401("token이 만료되었습니다")
//            }
//            if (authTokenService.verifyToken(prefixRemovedRefreshToken) != true) {
//                throw Jisik2n401("refresh token이 적절하지 않습니다.")
//            }
//
//            if (authTokenService.verifyToken(prefixRemovedAccessToken) == true) { // access token 정상적 작동
//                val userId = authTokenService.getCurrentUserId(prefixRemovedAccessToken)
//                val userEntity = userRepository.findByIdOrNull(userId)
//                request.setAttribute("userEntity", userEntity)
//            } else { // access token이 만료되었거나, 존재하지도 않거나
//
//                val tokenEntity = tokenRepository.findByAccessToken(prefixRemovedAccessToken)
//                if (tokenEntity == null) { // access token이 존재하지 않음
//
//                    throw Jisik2n401("access token이 적절하지 않습니다.")
//                } else { // access token 만료됨
//
//                    if (prefixRemovedRefreshToken == tokenEntity.refreshToken) {
//                        val newAccessToken = authTokenService.generateAccessTokenByUid(tokenEntity.keyUid)
//                        tokenEntity.accessToken = newAccessToken
//                        tokenRepository.save(tokenEntity)
//                        val userId = authTokenService.getCurrentUserId(newAccessToken)
//                        val userEntity = userRepository.findByIdOrNull(userId)
//                        request.setAttribute("userEntity", userEntity)
//                    } else {
//                        throw Jisik2n401("refresh token이 적절하지 않습니다.")
//                    }
//                }
//            }
//        }
//
//        return super.preHandle(request, response, handler)
//    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val handlerCasted = (handler as? HandlerMethod) ?: return true
        if (handlerCasted.hasMethodAnnotation(Authenticated::class.java)) {
            val accessToken = request.getHeader("Authorization") ?: throw Jisik2n400("access Token 획득 실패")
            val prefixRemovedAccessToken = accessToken.replace("Bearer ", "").trim { it <= ' ' }

            if (authTokenService.verifyToken(prefixRemovedAccessToken) == true) {
                val userId = authTokenService.getCurrentUserId(prefixRemovedAccessToken)
                val userEntity = userRepository.findByIdOrNull(userId)
                request.setAttribute("userEntity", userEntity)
            } else {
                throw Jisik2n401("access token이 적절하지 않습니다.")
            }
        }

        return super.preHandle(request, response, handler)
    }
}

package com.wafflestudio.team2.jisik2n.config

import com.wafflestudio.team2.jisik2n.common.Authenticated
import com.wafflestudio.team2.jisik2n.common.Jisik2n400
import com.wafflestudio.team2.jisik2n.common.Jisik2n401
import com.wafflestudio.team2.jisik2n.common.UserContext
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
    private val authInterceptor1: AuthInterceptor1,
    private val authInterceptor2: AuthInterceptor2,
    private val authArgumentResolver: AuthArgumentResolver,
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(authInterceptor1).order(1)
        registry.addInterceptor(authInterceptor2).order(2)
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
class AuthInterceptor1(
    private val authTokenService: AuthTokenService,
    private val userRepository: UserRepository,
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        // Authentication: 유저인지 확인한다 => accessToken 없으면 안주고, 틀렸으면 401, 맞으면 주고
        val accessToken = request.getHeader("Authorization")
        if (accessToken == null) {
            throw Jisik2n400("token을 제공하지 않았습니다")
        } else if (accessToken == "") {
            request.setAttribute("userEntity", null)
        } else {
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

@Configuration
class AuthInterceptor2 : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        // Authorization: 권한을 확인한다 => 유저정보 없으면 401, 있으면 컨트롤러 실행 가능
        val handlerCasted = (handler as? HandlerMethod) ?: return true

        if (handlerCasted.hasMethodAnnotation(Authenticated::class.java)) {
            if (request.getAttribute("userEntity") == null) {
                throw Jisik2n401("로그인을 해야 합니다")
            }
        }

        return super.preHandle(request, response, handler)
    }
}

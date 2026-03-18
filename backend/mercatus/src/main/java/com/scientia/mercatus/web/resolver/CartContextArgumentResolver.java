    package com.scientia.mercatus.web.resolver;

    import com.scientia.mercatus.dto.Cart.CartContextDto;
    import com.scientia.mercatus.entity.User;
    import com.scientia.mercatus.security.GuestAuthenticationToken;
    import com.scientia.mercatus.security.SpringSecurityAuthContext;
    import jakarta.servlet.http.HttpServletRequest;
    import lombok.RequiredArgsConstructor;
    import org.springframework.core.MethodParameter;
    import org.springframework.security.authentication.AnonymousAuthenticationToken;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.stereotype.Component;
    import org.springframework.web.bind.support.WebDataBinderFactory;
    import org.springframework.web.context.request.NativeWebRequest;
    import org.springframework.web.method.support.HandlerMethodArgumentResolver;
    import org.springframework.web.method.support.ModelAndViewContainer;

    @Component
    @RequiredArgsConstructor
    public class CartContextArgumentResolver implements HandlerMethodArgumentResolver {

        private final SpringSecurityAuthContext authContext;

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return parameter.getParameterType().equals(CartContextDto.class);
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = authContext.getCurrentUserIdOrNull();
            String sessionId = null;
            if (authentication instanceof GuestAuthenticationToken) {
                sessionId = authentication.getCredentials().toString();
            }

            return new CartContextDto(sessionId, userId);
        }
    }

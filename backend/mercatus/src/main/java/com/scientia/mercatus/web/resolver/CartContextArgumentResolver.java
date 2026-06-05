    package com.scientia.mercatus.web.resolver;

    import com.scientia.mercatus.dto.Cart.CartContextDto;
    import com.scientia.mercatus.filter.SessionFilter;
    import com.scientia.mercatus.security.GuestAuthenticationToken;
    import com.scientia.mercatus.security.SpringSecurityAuthContext;
    import lombok.RequiredArgsConstructor;
    import org.springframework.core.MethodParameter;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.stereotype.Component;
    import org.springframework.web.bind.support.WebDataBinderFactory;
    import org.springframework.web.context.request.NativeWebRequest;
    import org.springframework.web.method.support.HandlerMethodArgumentResolver;
    import org.springframework.web.method.support.ModelAndViewContainer;
    import jakarta.servlet.http.HttpServletRequest;

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
            Long userId = authContext.getCurrentUserIdOrNull();
            
            HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
            String sessionId = (String) request.getAttribute(SessionFilter.SESSION_ATTRIBUTE);

            return new CartContextDto(sessionId, userId);
        }
    }

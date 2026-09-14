package com.example.ecommerce;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DomainFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String host = request.getServerName();
        String uri = request.getRequestURI();

        // Static resources & APIs ko bypass karo taaki CSS/JS/images load ho sakein
        if (uri.startsWith("/api/") || uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".png") || uri.endsWith(".jpg")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1. SELLER DOMAIN (seller.indiansmarket.local)
        if ("seller.indiansmarket.local".equalsIgnoreCase(host)) {
            if (uri.equals("/") || uri.equals("/index.html")) {
                request.getRequestDispatcher("/seller/portal.html").forward(request, response);
                return;
            }
            if (uri.startsWith("/internal/")) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        // 2. INTERNAL OPS DOMAIN (corp-ops.indiansmarket.local)
        else if ("corp-ops.indiansmarket.local".equalsIgnoreCase(host)) {
            if (uri.equals("/") || uri.equals("/index.html")) {
                request.getRequestDispatcher("/internal/ops.html").forward(request, response);
                return;
            }
        }

        // 3. BUYER STOREFRONT (indiansmarket.local & localhost)
        else {
            if (uri.startsWith("/internal/")) {
                // Public domain se ops portal completely hidden (404)
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
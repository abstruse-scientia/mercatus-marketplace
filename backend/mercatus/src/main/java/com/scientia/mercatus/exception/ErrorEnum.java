package com.scientia.mercatus.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorEnum {

    //Related to HttpStatus code 404: Not found.
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "GEN_404_01", "Resource not found"),
    NO_LOGGED_IN_USER_FOUND(HttpStatus.NOT_FOUND, "USR_404_01", "No logged in user found"),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORD_404_01", "Order not found"),
    PRODUCT_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "PRD_404_01", "Product image not found"),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRD_404_02", "Product not found"),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH_404_01", "Token not found"),
    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "ADDR_404_01", "Address not found"),
    DEFAULT_ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "ADDR_404_02", "Default Address not found"),
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "CART_404_01", "Cart not found"),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CTG_404_01", "Category not found"),
    INVENTORY_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "INV_404_01", "Inventory item not found"),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "RES_404_01", "Reservation not found"),

    //Related to HttpStatus code 400: Bad request
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "GEN_400_01", "Invalid request"),
    ILLEGAL_QUANTITY(HttpStatus.BAD_REQUEST, "QTY_400_01", "Illegal quantity"),
    AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "PAY_400_01", "Amount mismatch"),
    RESERVATION_EXPIRED(HttpStatus.BAD_REQUEST, "RES_400_01", "Reservation expired"),
    INVALID_PAYMENT(HttpStatus.BAD_REQUEST, "PAY_400_02", "Invalid payment"),
    INVALID_RESERVATION(HttpStatus.BAD_REQUEST, "RES_400_02", "Invalid reservation"),

    //Related to HttpStatus code 409: Business Conflict
    CATEGORY_OR_SLUG_EXISTS(HttpStatus.CONFLICT, "CTG_409_01", "Category or slug already exists"),
    CATEGORY_NOT_EMPTY(HttpStatus.CONFLICT, "CTG_409_02", "Category has linked resources"),
    SLUG_NOT_UNIQUE(HttpStatus.CONFLICT, "SLG_409_01", "Slug not unique"),
    PRODUCT_ID_MISMATCH(HttpStatus.CONFLICT, "PROD_409_01", "Product ID mismatch"),
    IMAGE_PRODUCT_MISMATCH(HttpStatus.CONFLICT, "IMG_409_01", "Image does not belong to product"),
    IMAGE_COUNT_MISMATCH(HttpStatus.CONFLICT, "IMG_409_02", "Image count does not match"),
    PAYMENT_ALREADY_EXISTS(HttpStatus.CONFLICT, "PAY_409_01", "Payment already exists"),
    RESERVATION_EXISTS(HttpStatus.CONFLICT, "RES_409_01", "Reservation already exists"),
    INSUFFICIENT_STOCK(HttpStatus.CONFLICT, "INV_409_01", "Insufficient stock"),

    //Related to HttpStatus code 401: Unauthorized
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_401_01", "Token expired"),
    TOKEN_REVOKED(HttpStatus.UNAUTHORIZED, "AUTH_401_02", "Token revoked"),
    UNAUTHENTICATED_USER(HttpStatus.UNAUTHORIZED, "AUTH_401_03", "Unauthenticated user"),

    //Related to HttpStatus code 403: Forbidden
    FORBIDDEN_OPERATION(HttpStatus.FORBIDDEN, "GEN_403_01", "Operation not allowed"),

    //Related to HttpStatus code 502: Bad gateway
    PAYMENT_GATEWAY_ERROR(HttpStatus.BAD_GATEWAY, "PAY_502_01", "Payment gateway error"),

    //Related to HttpStatus code 500: Internal Server Error
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SYS_500_01", "Unexpected internal error");




    private final HttpStatus status;
    private final String internalCode;
    private final String message;

    ErrorEnum(HttpStatus status, String internalCode,String message) {
        this.status = status;
        this.internalCode = internalCode;
        this.message = message;
    }

}

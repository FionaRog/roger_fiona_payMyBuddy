package com.openclassroom.paymybuddy.exception;

/**
 * Exception métier générique utilisée pour signaler une erreur.
 *
 * <p>Cette exception est utilisée par les services de l'application pour rapporter
 * des problèmes métier (règles de domaine, validation, etc.) tout en conservant
 * un message d'erreur lisible.<p>
 *
 * Elle hérite de {@link RuntimeException} afin de ne pas imposer de bloc try/catch
 * systématique, et permet de centraliser la gestion des erreurs dans {@link GlobalExceptionHandler}.
 */
public class BusinessException extends RuntimeException{

    /**
     * Code d'erreur associé à cette exception.
     *
     * Permet de différencier visuellement le type d'erreur métier dans le log
     * ou dans l'interface utilisateur (par exemple "INVALID_OPERATION", "USER_NOT_FOUND").
     */
    private final String errorCode;

    /**
     * Construit une nouvelle exception métier.
     *
     * Le paramètre {@code message} est utilisé pour le message de base de l'exception
     * et permet un affichage standard dans les logs et les outils de débogage.
     *
     * @param errorCode le code d'erreur
     * @param message le message décrivant l'erreur (visible à l'utilisateur ou en log)
     */
    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Récupère le code d'erreur associé à cette exception.
     *
     * @return le code d'erreur associé à cette exception
     */
    public String getErrorCode() {
        return errorCode;
    }
}

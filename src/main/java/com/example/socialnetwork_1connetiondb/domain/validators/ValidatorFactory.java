package com.example.socialnetwork_1connetiondb.domain.validators;

/**
 * Validator factory.
 */
public class ValidatorFactory {
    private static ValidatorFactory instace;

    /**
     * Private constructor.
     */
    private ValidatorFactory(){

    }

    /**
     * Get desired validator.
     * @param strategy the validation strategy
     * @return the validator according to the strategy
     */
    public Validator getValidator(ValidatorStrategy strategy){
        return switch (strategy) {
            case USER -> new UserValidator();
            case FRIENDSHIP -> new FriendshipValidator();
            default -> null;
        };
    }

    /**
     *
     * @return the single class instance
     */
    public static ValidatorFactory getInstance(){
        if (instace == null){
            instace = new ValidatorFactory();
        }
        return instace;
    }
}

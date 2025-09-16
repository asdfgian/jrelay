package com.jrelay.ui.shared.utils.template;

/**
 * Defines a structural contract for building UI components or modules in a
 * standardized sequence.
 * <p>
 * Implementing classes must provide concrete implementations for initializing
 * components,
 * configuring styles, and composing the structure. Logic attachment is optional
 * and has a default
 * no-op implementation.
 * <p>
 * The {@link #build()} method executes the setup steps in the following order:
 * {@code initComponents()}, {@code configureStyle()}, {@code attachLogic()},
 * and {@code compose()}.
 * This ensures a consistent initialization pattern across all implementations.
 * 
 * @author ASDFG14N
 * @since 15-07-2025
 */
public interface Struct {

    void initComponents();

    void configureStyle();

    default void attachLogic() {
    }

    void compose();

    default void build() {
        initComponents();
        configureStyle();
        attachLogic();
        compose();
    }
}

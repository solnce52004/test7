package dev.example.test7.swagger;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.bean.validators.plugins.Validators;
import springfox.documentation.builders.StringElementFacetBuilder;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

import javax.validation.constraints.Email;
import java.util.Optional;

@Component
@Order(Validators.BEAN_VALIDATOR_PLUGIN_ORDER)
@Import(BeanValidatorPluginsConfiguration.class)

public class EmailAnnotationPlugin implements ModelPropertyBuilderPlugin {
    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }

    @Override
    public void apply(ModelPropertyContext context) {
        Optional<Email> email = springfox.bean.validators.plugins.Validators.annotationFromBean(context, Email.class);
        if (email.isPresent()) {
            context.getSpecificationBuilder()
                    .facetBuilder(StringElementFacetBuilder.class)
                    .pattern(email.get().regexp());

            context.getSpecificationBuilder()
                    .example("email@email.com");
        }
    }
}

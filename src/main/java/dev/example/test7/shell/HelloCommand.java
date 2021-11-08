package dev.example.test7.shell;

import dev.example.test7.dto.UserDTO;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * clean install -DskipTests
 */

@ShellComponent
@ShellCommandGroup("Hello Group")
public class HelloCommand {

    private boolean shutdownAvailable;

    /**
     * count --param1 5 --param2 6
     * count
     */
    @ShellMethod(
            value = "Count of two integers.",
            group = "Count subgroup"
    )
    public int count(
            @ShellOption(
                    value = {"-P1", "--param1"},
                    defaultValue = "111",
                    help = "Первый параметр"
            )
            @Max(500)
                    int a,

            @ShellOption(
                    value = {"-P2", "--param2"},
                    defaultValue = "222",
                    help = "Второй параметр"
            )
            @Min(0)
                    int b
    ) {
        final int sum = a + b;

        if (sum == 50) {
            shutdownAvailable = true;
        }

        return sum;
    }

    /**
     * arity 1 2 3
     * arity --params 1 2 3
     */
    @ShellMethod("Count arity.")
    public float arity(@ShellOption(arity = 3) float[] params) {
        return params[0] + params[1] + params[2];
    }

    /**
     * arity() от 0
     */
    @ShellMethod("Terminate the system.")
//    @ShellMethodAvailability("shutdownAvailabilityCheck") //либо тут, либо над методом проверки
    public String shutdown(
            @ShellOption(arity=1, defaultValue="false")
            boolean force
    ) {
        return "You said " + force;
    }

    /////////////////
    @ShellMethodAvailability({"shutdown"})
    public Availability shutdownAvailabilityCheck(){
        return shutdownAvailable
                ? Availability.available()
                : Availability.unavailable("**** you can not shutdown system! Count command not used or returned not 50! ****");
    }
    ///////////////


    /**
     * conversion-example "Ivan,pass,true"
     */
    @ShellMethod("Shows conversion using Spring converter")
    public String conversionExample(UserDTO user) {
        return user.toString();
    }
}


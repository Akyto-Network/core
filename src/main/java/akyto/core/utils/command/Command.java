package akyto.core.utils.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Command Framework - Command <br>
 * The command annotation used to designate methods as commands. All methods
 * should have a single CommandArgs argument
 *
 * @author minnymin3
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    long DEFAULT_COOLDOWN_MS = 1000;

    /**
     * The name of the command. If it is a sub command then its values would be
     * separated by periods. i.e. a command that would be a subcommand of test
     * would be 'test.subcommandname'
     *
     * @return
     */
    String name();

    /**
     * Gets the required permission of the command
     *
     * @return
     */
    String permission() default "";

    /**
     * The message sent to the profile when they do not have permission to
     * execute it
     *
     * @return
     */
    String noPerm() default "You do not have permission to perform that action.";

    /**
     * A list of alternate names that the command is executed under. See
     * name() for details on how names work
     *
     * @return
     */
    String[] aliases() default {};

    /**
     * The description that will appear in /help of the command
     *
     * @return
     */
    String description() default "";

    /**
     * The usage that will appear in /help (commandname)
     *
     * @return
     */
    String usage() default "";

    /**
     * Whether the command is available to players only
     *
     * @return
     */
    boolean inGameOnly() default false;

    /**
     * Cooldown between 2 executions of the command in ms (bypassable with permission "akyto.command.cooldown")
     *
     * @return
     */
    long coolDown() default DEFAULT_COOLDOWN_MS;
}

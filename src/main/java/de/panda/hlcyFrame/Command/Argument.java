package de.panda.hlcyFrame.Command;

import de.panda.hlcyFrame.Command.Parser.ArgumentParser;
import de.panda.hlcyFrame.Command.Parser.StringParser;
import lombok.Getter;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Argument {

    private String arg;
    private ArgumentType type = ArgumentType.STANDARD;
    private HlcyCommand command;
    @Getter
    private Argument parent;
    @Getter
    private Argument child;
    private int depth;
    private int minDepth;
    private ArgumentParser<?> parser = new StringParser();

    public Argument(String arg, ArgumentParser<?> parser) {
        this.parser = parser;
        this.arg = arg;
    }

    public Argument(String arg) {
        this.arg = arg;
    }

    public Argument setParent(Argument parent) {
        this.parent = parent;
        return this;
    }

    public Argument setChild(Argument child) {
        this.child = child;
        return this;
    }

    public Argument newArgument(String arg, ArgumentParser<?> parser) {
        Argument argument = new Argument(arg, parser).setParent(this).setDepth(this.depth + 1).setCommand(this.command);
        this.child = argument;
        return argument;
    }

    public Argument setDepth(int depth) {
        this.depth = depth;
        return this;
    }

    public Argument setMinDepth(int minDepth) {
        this.minDepth = minDepth;
        return this;
    }

    public Argument setInfinite() {
        this.minDepth = depth - 1;
        this.depth = 999;
        return this;
    }

    public Argument newArgument(String arg) {
        Argument argument = new Argument(arg).setParent(this).setDepth(this.depth + 1).setCommand(this.command);
        this.child = argument;
        return argument;
    }

    public Argument getArgumentAtDepth(int depth) {
        Argument result = this;
        if(depth < 1 || this.child == null) return result;
        return getArgumentAtDepth(depth - 1);
    }

    public Argument getPeak() {
        Argument result = this;
        if(this.parent == null) return result;
        return getPeak();
    }

    public int getMaxDepth() {
        int result = depth;
        if(!hasChild()) return result;
        return child.getMaxDepth();
    }

    public int getMinDepth(int depth) {
        if(!hasChild()) return Math.min(depth, this.minDepth);
        return this.child.getMinDepth(Math.min(depth, this.minDepth)) + 1;
    }

    public boolean hasChild() {
        return this.child != null;
    }

    public boolean isPeak() {
        return this.parent == null;
    }

    public <T> T parse(String input) {
        return (T) parser.parse(input);
    }


    public Argument setCommand(HlcyCommand cmd) {
        this.command = cmd;
        cmd.addParser(depth, parser);
        return this;
    }

    public HlcyCommand command() {
        return command;
    }

    public boolean isUsed(String[] input) {
        return input.length >= depth;
    }

    public Argument back() {
        return parent != null ? parent : this;
    }
}

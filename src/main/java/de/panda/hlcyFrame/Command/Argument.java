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

    public Argument(String arg) {
        this.arg = arg;
    }

    protected Argument setParent(Argument parent) {
        this.parent = parent;
        return this;
    }

    protected Argument setChild(Argument child) {
        this.child = child;
        return this;
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

    protected Argument getArgumentAtDepth(int depth) {
        Argument result = this;
        if(depth < 1 || this.child == null) return result;
        return getArgumentAtDepth(depth - 1);
    }

    protected Argument getPeak() {
        Argument result = this;
        if(this.parent == null) return result;
        return getPeak();
    }

    protected int getMaxDepth() {
        int result = depth;
        if(!hasChild()) return result;
        return child.getMaxDepth();
    }

    protected int getMinDepth(int depth) {
        if(!hasChild()) return Math.min(depth, this.minDepth);
        return this.child.getMinDepth(Math.min(depth, this.minDepth)) + 1;
    }

    protected boolean hasChild() {
        return this.child != null;
    }

    protected boolean isPeak() {
        return this.parent == null;
    }

    protected Argument setCommand(HlcyCommand cmd) {
        this.command = cmd;
        return this;
    }

    public HlcyCommand command() {
        return command;
    }

    protected boolean isUsed(String[] input) {
        return input.length >= depth;
    }

    public Argument back() {
        return parent != null ? parent : this;
    }
}

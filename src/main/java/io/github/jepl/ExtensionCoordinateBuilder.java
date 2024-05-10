package io.github.jepl;

import io.github.jepl.annotation.ExtensionPoint;

@ExtensionPoint
public interface ExtensionCoordinateBuilder<E> {

    ExtensionCoordinate build(E context);
}

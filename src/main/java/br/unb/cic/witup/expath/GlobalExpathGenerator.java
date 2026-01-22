package br.unb.cic.witup.expath;

import java.util.List;

public interface GlobalExpathGenerator {
    List<GlobalExpath> generateForClass(String className);

    List<GlobalExpath> generateForMethod(String className, String methodSignature);

    List<GlobalExpath> fromLocalExpaths(List<LocalExpath> locals);
}

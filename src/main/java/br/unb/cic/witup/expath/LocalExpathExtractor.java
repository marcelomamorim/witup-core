package br.unb.cic.witup.expath;

import java.util.List;

public interface LocalExpathExtractor {
    List<LocalExpath> extract(String className, String methodSignature);
}

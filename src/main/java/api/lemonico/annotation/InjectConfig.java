package api.lemonico.annotation;

import org.seasar.doma.AnnotateWith;
import org.seasar.doma.Annotation;
import org.seasar.doma.AnnotationTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@AnnotateWith(annotations = {
		// 生成されたDAO実装クラスに@Component
		@Annotation(target = AnnotationTarget.CLASS, type = Component.class),
		// 生成されたDAO実装クラスのコンストラクタに@Autowired
		@Annotation(target = AnnotationTarget.CONSTRUCTOR, type = Autowired.class) })
public @interface InjectConfig {
}
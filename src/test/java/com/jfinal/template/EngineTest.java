package com.jfinal.template;

import com.jfinal.kit.JMap;
import com.jfinal.kit.PathKit;

public class EngineTest {
	
	public static void main(String[] args) {
		Engine engine = Engine.use().setBaseTemplatePath(PathKit.getWebRootPath());
		Template template = engine.getTemplateByString("#(value)");
		String ret = template.renderToString(JMap.create("value", 123));
		System.out.println(ret);
	}
}

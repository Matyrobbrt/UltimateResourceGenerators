package com.matyrobbrt.urg.generator.misc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RenderInfo {

	@Expose
	@SerializedName("uses_default_renderer")
	public boolean usesDefaultRender = false;

	@Expose
	public double[] translation = {
			0.5d, 0.3d, 0.5d
	};

	@Expose
	public float scale = 0.8f;

}

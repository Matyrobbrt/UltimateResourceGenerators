package com.matyrobbrt.urg.data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.matyrobbrt.urg.generator.misc.GeneratorInfo;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.loading.FMLPaths;

public class URGGeneratorProvider implements IDataProvider {

	public final String packName;

	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	public final DataGenerator dataGenerator;

	protected URGGeneratorProvider(DataGenerator dataGenerator, String packName) {
		this.dataGenerator = dataGenerator;
		this.packName = packName;
	}

	public final Map<ResourceLocation, GeneratorInfo> generatorInfo = new HashMap<>();

	@Override
	public void run(DirectoryCache pCache) throws IOException {
		createInfo();

		generateGenerators(pCache);
	}

	protected void createInfo() {
	}

	private void generateGenerators(DirectoryCache cache) {
		Path outputFolder = Paths.get(FMLPaths.GAMEDIR.get().resolve("urg_packs").toFile().getPath());
		generatorInfo.forEach((rl, info) -> {
			Path path = outputFolder
					.resolve(packName + "/data/" + rl.getNamespace() + "/urg_generators/" + rl.getPath() + ".json");
			try {
				IDataProvider.save(GSON, cache, GSON.toJsonTree(info), path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public static GeneratorInfo createInfo(Consumer<GeneratorInfo> consumer) {
		GeneratorInfo info = new GeneratorInfo();
		consumer.accept(info);
		return info;
	}

	@Override
	public String getName() { return "URGGeneratorProvider"; }

}

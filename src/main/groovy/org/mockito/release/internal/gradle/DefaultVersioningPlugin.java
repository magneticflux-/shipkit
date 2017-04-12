package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.mockito.release.gradle.VersioningPlugin;
import org.mockito.release.internal.gradle.util.TaskMaker;
import org.mockito.release.version.Version;
import org.mockito.release.version.VersionFile;

import java.io.File;

public class DefaultVersioningPlugin implements VersioningPlugin {

    private static Logger LOG = Logging.getLogger(DefaultVersioningPlugin.class);

    public void apply(Project project) {
        //TODO "version.properties" is hardcoded all over the place.
        // At the very least we should have a constant in this plugin.
        final File versionFile = new File(project.getRootDir(), "version.properties");
        VersionFile versionInfo = Version.versionFile(versionFile);

        //TODO let's add unit tests
        project.getExtensions().add(VersionFile.class.getName(), versionInfo);
        project.getExtensions().getExtraProperties().set("release_notable", versionInfo.isNotableRelease());

        final String version = versionInfo.getVersion();
        LOG.lifecycle("  Building version '{}' (value loaded from '{}' file).", version, versionFile.getName());

        project.allprojects(new Action<Project>() {
            @Override
            public void execute(Project project) {
                project.setVersion(version);
            }
        });

        TaskMaker.task(project, "bumpVersionFile", DefaultBumpVersionFileTask.class, new Action<DefaultBumpVersionFileTask>() {
            public void execute(DefaultBumpVersionFileTask t) {
                t.setVersionFile(versionFile);
                t.setDescription("Increments version number in " + versionFile.getName());
            }
        });
    }

}
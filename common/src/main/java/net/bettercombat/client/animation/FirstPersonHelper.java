package net.bettercombat.client.animation;

import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonConfiguration;

public class FirstPersonHelper {
    public static FirstPersonConfiguration mirrored(FirstPersonConfiguration config) {
        return new FirstPersonConfiguration(
                // Switched up order on purpose
                config.isShowLeftArm(), config.isShowRightArm(), config.isShowLeftItem(), config.isShowRightItem()
        );
    }
}

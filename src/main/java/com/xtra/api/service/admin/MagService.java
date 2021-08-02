package com.xtra.api.service.admin;

import com.xtra.api.model.mag.MagDevice;
import com.xtra.api.repository.ConnectionRepository;
import com.xtra.api.repository.MagRepository;
import com.xtra.api.service.CrudService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log4j2
public class MagService extends CrudService<MagDevice, Long, MagRepository> {
    private final ConnectionRepository connectionRepository;

    protected MagService(MagRepository repository, ConnectionRepository connectionRepository) {
        super(repository, "Mag");
        this.connectionRepository = connectionRepository;
    }

    public ResponseEntity<?> handlePortalRequest(String userIp, String userAgent, String requestType, String requestAction, String serialNumber, String mac, String version, String stbType, String imageVersion, String deviceId, String deviceId2, String hwVersion, String gMode) {
        var magDevice = authenticateMagDevice(serialNumber, mac, version, stbType, imageVersion, deviceId, deviceId2, hwVersion, userIp);
//        if (magDevice.isEmpty())
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        var response = new JSONObject();

        switch (requestType) {
            case "stb":
                switch (requestAction) {
                    case "handshake":
                        String token = DigestUtils.md5Hex(UUID.randomUUID().toString());
                        if (magDevice.isPresent()) {
                            var mag = magDevice.get();
                            mag.setToken(token);
                            repository.save(mag);
                        }
                        response = response.put("js", new JSONObject().put("token", token));
                        break;
                    case "get_ad":
                    case "get_storages":
                        response = response.put("js", "");
                        break;
                    case "get_profile":
                        response = response.put("js", new JSONObject().put("profile", getProfile()));
                        break;
                    case "get_localization":
                        response = response.put("js", getLocalization());
                        break;
                    case "log":
                        response = response.put("js", true);
                        break;
                    case "get_modules":
                        var modules = new JSONArray()
                                .put(new JSONObject().put("all_modules", Arrays.asList("media_browser", "tv", "vclub", "sclub", "radio", "apps", "youtube", "dvb",
                                        "tv_archive", "time_shift", "time_shift_local", "epg.reminder", "epg.recorder", "epg", "epg.simple", "audioclub",
                                        "downloads_dialog", "downloads", "karaoke", "weather.current", "widget.audio", "widget.radio", "records", "remotepvr",
                                        "pvr_local", "settings.parent", "settings.localization", "settings.update", "settings.playback", "settings.common",
                                        "settings.network_status", "settings", "course.nbu", "weather.day", "cityinfo", "horoscope", "anecdote", "game.mastermind",
                                        "account", "demo", "infoportal", "internet", "service_management", "logout", "account_menu")))
                                .put(new JSONObject().put("switchable_modules", Arrays.asList("sclub", "vlub", "karaoke", "cityinfo", "horoscope", "anecdote", "game.mastermind")))
                                .put(new JSONObject().put("disabled_modules", Arrays.asList("weather.current", "weather.day", "cityinfo", "karaoke", "game.mastermind", "records", "downloads", "remotepvr", "service_management", "settings.update", "settings.common", "audioclub", "course.nbu", "infoportal", "demo", "widget.audio", "widget.radio")))
                                .put(new JSONObject().put("restricted_modules", ""))
                                //template
                                .put(new JSONObject().put("launcher_url", ""))
                                //set address
                                .put(new JSONObject().put("launcher_profile_url", ""));
                        response = response.put("js", modules);
                        break;
                    case "get_preload_images":
                        break;
                    case "get_settings_profile":
                        break;
                    case "get_locales":
                        break;
                    case "get_countries":
                    case "search_cities":
                    case "get_cities":
                        response = response.put("js", "");
                        break;
                    case "get_timezones":
                        break;
                    case "get_tv_aspects":
                        break;
                    case "set_volume":
                        break;
                    case "set_aspect":
                        break;
                    case "set_stream_error":
                        break;
                    case "set_screensaver_delay":
                        break;
                    case "set_playback_buffer":
                        break;
                    case "set_plasma_saving":
                        break;
                    case "set_parent_password":
                        break;
                    case "set_locale":
                        break;
                    case "set_hdmi_reaction":
                        break;
                }
                break;
            case "watchdog":
                switch (requestAction) {
                    case "get_events":
                        break;
                    case "confirm_event":
                        break;
                }
                break;
            case "audioclub":
                switch (requestAction) {

                }
            case "itv":
                switch (requestAction) {

                }
            case "remote_pvr":
                switch (requestAction) {

                }
            case "media_favorites":
                switch (requestAction) {

                }
            case "tvreminder":
                switch (requestAction) {

                }
            case "vod":
                switch (requestAction) {

                }
            case "series":
                switch (requestAction) {

                }
            case "downloads":
                switch (requestAction) {

                }
            case "weatherco":
                switch (requestAction) {

                }
            case "course":
                switch (requestAction) {

                }
            case "account_info":
                switch (requestAction) {

                }
            case "radio":
                switch (requestAction) {

                }
            case "tv_archive":
                switch (requestAction) {

                }
            case "epg":
                switch (requestAction) {

                }

        }
        return ResponseEntity.ok(response.toString());
    }

    private JSONObject getLocalization() {
        var locale = "en_GB.utf8";
        var response = new JSONObject();
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream("mag_strings.xml");
            SAXBuilder sax = new SAXBuilder();
            Document doc = sax.build(is);
            Element rootNode = doc.getRootElement();
            List<Element> stringSets = rootNode.getChildren("stringSet");
            var stringSet = stringSets.stream().filter(element -> element.getAttributeValue("locale").equals(locale)).findFirst();
            if (stringSet.isPresent()) {
                stringSet.get().getChildren("text").forEach(element -> response.put(element.getAttributeValue("key"), element.getText()));
                var textArrays = stringSet.get().getChildren("textArray");
                textArrays.forEach(element -> {
                    response.put(element.getAttributeValue("key"), new JSONArray().putAll(
                            element.getChildren().stream().map(Element::getText).collect(Collectors.toList())));
                });
            }
        } catch (IOException | JDOMException e) {
            log.error("Could not parse mag_strings file!");
        }
        return response;
    }

    private JSONObject getProfile() {
        String locale = "";
        String status = "";
        var alwaysEnabledSubtitles = false;
        return new JSONObject()
                .put("id", "")
                .put("name", "")
                .put("sname", "")
                .put("pass", "")
                .put("parent_password", "")
                .put("bright", "")
                .put("contrast", "")
                .put("saturation", "")
                .put("video_out", "")
                .put("volume", "")
                .put("playback_buffer_bytes", "")
                .put("playback_buffer_size", "")
                .put("audio_out", "")
                .put("mac", "")
                .put("ip", "")
                .put("ls", "")
                .put("version", "")
                .put("lang", "")
                .put("locale", locale)
                .put("city_id", "")
                .put("hd", "")
                .put("main_notify", "")
                .put("fav_itv_on", "")
                .put("now_playing_start", "")
                .put("now_playing_type", "")
                .put("now_playing_content", "")
                .put("additional_services_on", "1")
                .put("time_last_play_tv", "0000-00-00 00:00:00")
                .put("time_last_play_video", "0000-00-00 00:00:00")
                .put("operator_id", "0")
                .put("storage_name", "")
                .put("hd_content", "0")
                .put("image_version", "undefined")
                .put("last_change_status", "0000-00-00 00:00:00")
                .put("last_start", "2018-02-18 17:33:38")
                .put("last_active", "2018-02-18 17:33:43")
                .put("keep_alive", "2018-02-18 17:33:43")
                .put("screensaver_delay", "10")
                .put("phone", "")
                .put("fname", "")
                .put("login", "")
                .put("password", "")
                .put("stb_type", "")
                .put("num_banks", "0")
                .put("tariff_plan_id", "0")
                .put("comment", "")
                .put("now_playing_link_id", "0")
                .put("now_playing_streamer_id", "0")
                .put("just_started", "1")
                .put("last_watchdog", "2018-02-18 17:33:39")
                .put("created", "2018-02-18 14:40:12")
                .put("plasma_saving", "0")
                .put("ts_enabled", "0")
                .put("ts_enable_icon", "1")
                .put("ts_path", "")
                .put("ts_max_length", "3600")
                .put("ts_buffer_use", "cyclic")
                .put("ts_action_on_exit", "no_save")
                .put("ts_delay", "on_pause")
                .put("video_clock", "Off")
                .put("verified", "0")
                .put("hdmi_event_reaction", 1)
                .put("pri_audio_lang", "")
                .put("sec_audio_lang", "")
                .put("pri_subtitle_lang", "")
                .put("sec_subtitle_lang", "")
                .put("subtitle_color", "16777215")
                .put("subtitle_size", "20")
                .put("show_after_loading", "")
                .put("play_in_preview_by_ok", "")
                .put("hw_version", "undefined")
                .put("openweathermap_city_id", "0")
                .put("theme", "")
                .put("settings_password", "0000")
                .put("expire_billing_date", "0000-00-00 00:00:00")
                .put("reseller_id", "")
                .put("account_balance", "")
                .put("client_type", "STB")
                .put("hw_version_2", "62")
                .put("blocked", "0")
                .put("units", "metric")
                .put("tariff_expired_date", "")
                .put("tariff_id_instead_expired", "")
                .put("activation_code_auto_issue", "1")
                .put("last_itv_id", 0)
                .put("updated", new JSONArray().put(new JSONObject().put("id", "1")).put(new JSONObject().put("anec", "0")).put(new JSONObject().put("vclub", "0")))
                .put("rtsp_type", "4")
                .put("rtsp_flags", "0")
                .put("stb_lang", "en")
                .put("display_menu_after_loading", "")
                .put("record_max_length", 180)
                .put("web_proxy_host", "")
                .put("web_proxy_port", "")
                .put("web_proxy_user", "")
                .put("web_proxy_pass", "")
                .put("web_proxy_exclude_list", "")
                .put("demo_video_url", "")
                .put("tv_quality_filter", "")
                .put("is_moderator", false)
                .put("timeslot_ratio", 0.33333333333333)
                .put("timeslot", 40)
                .put("kinopoisk_rating", "1")
                .put("enable_tariff_plans", "")
                .put("strict_stb_type_check", "")
                .put("cas_type", 0)
                .put("cas_params", "")
                .put("cas_web_params", "")
                .put("cas_additional_params", "[]")
                .put("cas_hw_descrambling", 0)
                .put("cas_ini_file", "")
                .put("logarithm_volume_control", "")
                .put("allow_subscription_from_stb", "1")
                .put("deny_720p_gmode_on_mag200", "1")
                .put("enable_arrow_keys_setpos", "1")
                .put("show_purchased_filter", "")
                .put("timezone_diff", 0)
                .put("enable_connection_problem_indication", "1")
                .put("invert_channel_switch_direction", "")
                .put("play_in_preview_only_by_ok", false)
                .put("enable_stream_error_logging", "")
                .put("always_enabled_subtitles", alwaysEnabledSubtitles)
                .put("enable_service_button", "")
                .put("enable_setting_access_by_pass", "")
                .put("tv_archive_continued", "")
                .put("plasma_saving_timeout", "600")
                .put("show_tv_only_hd_filter_option", "")
                .put("tv_playback_retry_limit", "0")
                .put("fading_tv_retry_timeout", "1")
                .put("epg_update_time_range", 0.6)
                .put("store_auth_data_on_stb", false)
                .put("account_page_by_password", "")
                .put("tester", false)
                .put("enable_stream_losses_logging", "")
                .put("external_payment_page_url", "")
                .put("max_local_recordings", "10")
                .put("tv_channel_default_aspect", "fit")
                .put("default_led_level", "10")
                .put("standby_led_level", "90")
                .put("show_version_in_main_menu", "1")
                .put("disable_youtube_for_mag200", "1")
                .put("auth_access", false)
                .put("epg_data_block_period_for_stb", "5")
                .put("standby_on_hdmi_off", "1")
                .put("force_ch_link_check", "")
                .put("stb_ntp_server", "pool.ntp.org")
                .put("overwrite_stb_ntp_server", "")
                .put("hide_tv_genres_in_fullscreen", "")
                .put("advert", "")
                .put("status", status)
                //read these from setting
                .put("update_url", "")
                .put("test_download_url", "")
                .put("default_timezone", "")
                .put("default_locale", "")
                .put("allowed_stb_types", "")
                .put("allowed_stb_types_for_local_recording", "")
                .put("storages", "")
                .put("tv_channel_default_aspect", "")
                .put("playback_limit", "")
                .put("enable_playback_limit", "")
                .put("show_tv_channel_logo", "")
                .put("show_channel_logo_in_preview", "")
                .put("enable_connection_problem_indication", "")
                .put("hls_fast_start", "1")
                .put("check_ssl_certificate", 0)
                .put("enable_buffering_indication", 1)
                .put("watchdog_timeout", "");
    }

    private Optional<MagDevice> authenticateMagDevice(String serialNumber, String mac, String version, String stbType, String imageVersion, String deviceId, String deviceId2, String hwVersion, String userIp) {
        var magRecord = repository.findByMac(mac);
        if (magRecord.isEmpty())
            return Optional.empty();
        var mag = magRecord.get();
        var line = mag.getLine();
        var currentConnections = connectionRepository.countAllByLineId(line.getId());
        if (!line.isExpired() && !line.isBanned() && !line.isBlocked() && line.getMaxConnections() != 0 && line.getMaxConnections() >= currentConnections)
            return Optional.of(mag);
        return Optional.empty();
    }

    @Override
    protected Page<MagDevice> findWithSearch(String search, Pageable page) {
        return null;
    }
}

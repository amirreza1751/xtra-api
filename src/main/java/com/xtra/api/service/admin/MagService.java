package com.xtra.api.service.admin;

import com.xtra.api.model.mag.MagDevice;
import com.xtra.api.repository.ConnectionRepository;
import com.xtra.api.repository.MagEventRepository;
import com.xtra.api.repository.MagRepository;
import com.xtra.api.service.CrudService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class MagService extends CrudService<MagDevice, Long, MagRepository> {
    private final ConnectionRepository connectionRepository;
    private final MagEventRepository magEventRepository;

    protected MagService(MagRepository repository, ConnectionRepository connectionRepository, MagEventRepository magEventRepository) {
        super(repository, "Mag");
        this.connectionRepository = connectionRepository;
        this.magEventRepository = magEventRepository;
    }

    public ResponseEntity<?> handlePortalRequest(String userIp, String userAgent, String token, String requestType, String requestAction, String serialNumber, String mac, String version, String stbType, String imageVersion, String deviceId, String deviceId2, String hwVersion, String gMode, Long eventActiveId) {
        var magDevice = authenticateMagDevice(token, serialNumber, mac, version, stbType, imageVersion, deviceId, deviceId2, hwVersion, userIp);
//        if (magDevice.isEmpty())
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        //@todo read from setting or default
        var stalkerTheme = "default";
        var response = new JSONObject();

        switch (requestType) {
            case "stb":
                switch (requestAction) {
                    case "handshake":
                        String newToken = DigestUtils.md5Hex(UUID.randomUUID().toString()).toUpperCase();
                        if (magDevice.isPresent()) {
                            var mag = magDevice.get();
                            mag.setToken(newToken);
                            repository.save(mag);
                        }
                        response = response.put("js", new JSONObject().put("token", newToken));
                        break;
                    case "get_ad":
                    case "get_storages":
                        response = response.put("js", "");
                        break;
                    case "get_profile":
                        response = response.put("js", getProfile(magDevice.isPresent()));
                        break;
                    case "get_localization":
                        response = response.put("js", getLocalization());
                        break;
                    case "log":
                        response = response.put("js", true);
                        break;
                    case "get_modules":
                        var modules = new JSONObject().put("all_modules", Arrays.asList("media_browser", "tv", "vclub", "sclub", "radio", "apps", "youtube", "dvb",
                                "tv_archive", "time_shift", "time_shift_local", "epg.reminder", "epg.recorder", "epg", "epg.simple", "audioclub",
                                "downloads_dialog", "downloads", "karaoke", "weather.current", "widget.audio", "widget.radio", "records", "remotepvr",
                                "pvr_local", "settings.parent", "settings.localization", "settings.update", "settings.playback", "settings.common",
                                "settings.network_status", "settings", "course.nbu", "weather.day", "cityinfo", "horoscope", "anecdote", "game.mastermind",
                                "account", "demo", "infoportal", "internet", "service_management", "logout", "account_menu"))
                                .put("switchable_modules", Arrays.asList("sclub", "vlub", "karaoke", "cityinfo", "horoscope", "anecdote", "game.mastermind"))
                                .put("disabled_modules", Arrays.asList("weather.current", "weather.day", "cityinfo", "karaoke", "game.mastermind", "records", "downloads", "remotepvr", "service_management", "settings.update", "settings.common", "audioclub", "course.nbu", "infoportal", "demo", "widget.audio", "widget.radio"))
                                .put("restricted_modules", new JSONArray())
                                .put("template", stalkerTheme)
                                .put("launcher_url", "")
                                //set address ??
                                .put("launcher_profile_url", "");
                        response = response.put("js", modules);
                        break;
                    case "get_preload_images":
                        var mod = gMode != null ? "i_" + gMode : "i";
                        var links = Arrays.asList("template/{0}/{1}/loading.png", "template/{0}/{1}/horoscope_menu_button_1_6_a.png", "template/{0}/{1}/ico_info.png", "template/{0}/{1}/mb_pass_bg.png", "template/{0}/{1}/mm_ico_info.png", "template/{0}/{1}/footer_menu_act.png", "template/{0}/{1}/_2_cloudy.png", "template/{0}/{1}/footer_sidepanel.png", "template/{0}/{1}/footer_search.png", "template/{0}/{1}/v_menu_1a.png", "template/{0}/{1}/loading_bg.gif", "template/{0}/{1}/horoscope_menu_button_1_11_b.png", "template/{0}/{1}/mb_table_act01.png", "template/{0}/{1}/horoscope_menu_button_1_11_a.png", "template/{0}/{1}/tv_table.png", "template/{0}/{1}/vol_1.png", "template/{0}/{1}/mb_prev_bg.png", "template/{0}/{1}/horoscope_menu_button_1_8_b.png", "template/{0}/{1}/mm_ico_youtube.png", "template/{0}/{1}/horoscope_menu_button_1_4_a.png", "template/{0}/{1}/tv_table_arrows.png", "template/{0}/{1}/horoscope_menu_button_1_9_a.png", "template/{0}/{1}/horoscope_menu_button_1_10_a.png", "template/{0}/{1}/1x1.gif", "template/{0}/{1}/mm_ico_karaoke.png", "template/{0}/{1}/mm_ico_video.png", "template/{0}/{1}/mb_table05.png", "template/{0}/{1}/mb_table_act02.png", "template/{0}/{1}/tv_table_separator.png", "template/{0}/{1}/mb_icons.png", "template/{0}/{1}/footer_btn.png", "template/{0}/{1}/horoscope_menu_button_1_5_b.png", "template/{0}/{1}/mm_ico_audio.png", "template/{0}/{1}/_7_hail.png", "template/{0}/{1}/mb_table_act05.png", "template/{0}/{1}/_9_snow.png", "template/{0}/{1}/v_menu_4.png", "template/{0}/{1}/_3_pasmurno.png", "template/{0}/{1}/low_q.png", "template/{0}/{1}/mm_ico_setting.png", "template/{0}/{1}/mb_context_borders.png", "template/{0}/{1}/input_episode_bg.png", "template/{0}/{1}/mb_table_act04.png", "template/{0}/{1}/mm_hor_bg3.png", "template/{0}/{1}/black85.png", "template/{0}/{1}/pause_btn.png", "template/{0}/{1}/ico_error26.png", "template/{0}/{1}/input_episode.png", "template/{0}/{1}/epg_red_mark.png", "template/{0}/{1}/footer_sidepanel_act.png", "template/{0}/{1}/horoscope_menu_button_1_3_b.png", "template/{0}/{1}/mb_pass_input.png", "template/{0}/{1}/footer_bg2.png", "template/{0}/{1}/osd_bg.png", "template/{0}/{1}/epg_orange_mark.png", "template/{0}/{1}/mm_ico_mb.png", "template/{0}/{1}/ears_arrow_l.png", "template/{0}/{1}/hr_filminfo.png", "template/{0}/{1}/mm_ico_rec.png", "template/{0}/{1}/mm_ico_account.png", "template/{0}/{1}/mb_icon_rec.png", "template/{0}/{1}/mm_hor_left.png", "template/{0}/{1}/mb_table04.png", "template/{0}/{1}/mb_player.png", "template/{0}/{1}/footer_search_act2.png", "template/{0}/{1}/input_channel_bg.png", "template/{0}/{1}/horoscope_menu_button_1_12_a.png", "template/{0}/{1}/horoscope_menu_button_1_9_b.png", "template/{0}/{1}/mm_ico_android.png", "template/{0}/{1}/bg.png", "template/{0}/{1}/mm_hor_right.png", "template/{0}/{1}/mb_quality.png", "template/{0}/{1}/mb_table02.png", "template/{0}/{1}/bg2.png", "template/{0}/{1}/horoscope_menu_button_1_1_a.png", "template/{0}/{1}/osd_line_pos.png", "template/{0}/{1}/input_channel.png", "template/{0}/{1}/horoscope_menu_button_1_7_a.png", "template/{0}/{1}/arr_right.png", "template/{0}/{1}/mm_ico_radio.png", "template/{0}/{1}/ico_confirm.png", "template/{0}/{1}/osd_btn.png", "template/{0}/{1}/osd_time.png", "template/{0}/{1}/footer_menu.png", "template/{0}/{1}/volume_off.png", "template/{0}/{1}/btn2.png", "template/{0}/{1}/mm_ico_internet.png", "template/{0}/{1}/volume_bg.png", "template/{0}/{1}/horoscope_menu_button_1_1_b.png", "template/{0}/{1}/v_menu_2b.png", "template/{0}/{1}/horoscope_menu_button_1_3_a.png", "template/{0}/{1}/horoscope_menu_button_1_4_b.png", "template/{0}/{1}/_255_NA.png", "template/{0}/{1}/_1_sun_cl.png", "template/{0}/{1}/horoscope_menu_button_1_10_b.png", "template/{0}/{1}/25alfa_20.png", "template/{0}/{1}/mb_table_act06.png", "template/{0}/{1}/input.png", "template/{0}/{1}/tv_table_focus.png", "template/{0}/{1}/skip.png", "template/{0}/{1}/epg_green_mark.png", "template/{0}/{1}/mm_vert_cell.png", "template/{0}/{1}/_1_moon_cl.png", "template/{0}/{1}/modal_bg.png", "template/{0}/{1}/_4_short_rain.png", "template/{0}/{1}/ears_arrow_r.png", "template/{0}/{1}/mm_ico_default.png", "template/{0}/{1}/osd_line.png", "template/{0}/{1}/mb_table07.png", "template/{0}/{1}/mm_ico_usb.png", "template/{0}/{1}/mb_context_bg.png", "template/{0}/{1}/footer_sidepanel_r.png", "template/{0}/{1}/horoscope_menu_button_1_2_a.png", "template/{0}/{1}/v_menu_1b.png", "template/{0}/{1}/mb_table03.png", "template/{0}/{1}/mb_table_act03.png", "template/{0}/{1}/mb_table01.png", "template/{0}/{1}/mm_ico_dm.png", "template/{0}/{1}/horoscope_menu_button_1_5_a.png", "template/{0}/{1}/horoscope_menu_button_1_6_b.png", "template/{0}/{1}/footer_sidepanel_l.png", "template/{0}/{1}/footer_sidepanel_line.png", "template/{0}/{1}/mm_ico_tv.png", "template/{0}/{1}/mb_table06.png", "template/{0}/{1}/mb_scroll_bg.png", "template/{0}/{1}/_8_rain_swon.png", "template/{0}/{1}/mb_scroll.png", "template/{0}/{1}/v_menu_2a.png", "template/{0}/{1}/v_menu_5.png", "template/{0}/{1}/horoscope_menu_button_1_2_b.png", "template/{0}/{1}/_10_heavy_snow.png", "template/{0}/{1}/aspect_bg.png", "template/{0}/{1}/_0_moon.png", "template/{0}/{1}/volume_bar.png", "template/{0}/{1}/v_menu_3.png", "template/{0}/{1}/mm_hor_bg1.png", "template/{0}/{1}/horoscope_menu_button_1_12_b.png", "template/{0}/{1}/mm_ico_ex.png", "template/{0}/{1}/footer_bg.png", "template/{0}/{1}/footer_sidepanel_arr.png", "template/{0}/{1}/mb_icon_scrambled.png", "template/{0}/{1}/ico_alert.png", "template/{0}/{1}/mm_ico_apps.png", "template/{0}/{1}/input_act.png", "template/{0}/{1}/ears.png", "template/{0}/{1}/horoscope_menu_button_1_8_a.png", "template/{0}/{1}/mm_hor_bg2.png", "template/{0}/{1}/arr_left.png", "template/{0}/{1}/horoscope_menu_button_1_7_b.png", "template/{0}/{1}/footer_search_act.png", "template/{0}/{1}/_0_sun.png", "template/{0}/{1}/_6_lightning.png", "template/{0}/{1}/osd_rec.png", "template/{0}/{1}/tv_prev_bg.png", "template/{0}/{1}/_5_rain.png");
                        links = links.stream().map(s -> String.format(s, stalkerTheme, mod)).collect(Collectors.toList());
                        var images = new JSONArray().putAll(links);
                        response = response.put("js", images);
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
                long magId = 0;
                if (magDevice.isPresent()) {
                    var mag = magDevice.get();
                    mag.setLastWatchdog(LocalDateTime.now());
                    repository.save(mag);
                    magId = mag.getId();
                }
                switch (requestAction) {
                    case "get_events":
                        var magEvent = magEventRepository.findFirstByMagDeviceIdAndStatusOrderById(magId, 0);
                        var data = new JSONObject();
                        if (magEvent.isPresent()) {
                            var event = magEvent.get();
                            var msgs = magEventRepository.countAllByMagDeviceIdAndStatus(magId, 0);
                            data.put("data", new JSONObject().put("msgs", msgs).put("id", event.getId()).put("event", event.getEvent()).put("need_confirm", event.isNeedsConfirm() ? 1 : 0)
                                    .put("msg", event.getMsg()).put("reboot_after_ok", event.isRebootAfterOk() ? 1 : 0).put("auto_hide_timeout", event.isAutoHideTimeout() ? 1 : 0).put("send_time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                                    .put("additional_services_on", event.isAdditionalServicesOn() ? 1 : 0).put("updated", new JSONObject().put("anec", event.isAnec() ? 1 : 0).put("vclub", event.isVclub() ? 1 : 0)));
                            switch (event.getEvent()) {
                                case "reboot":
                                case "reload_portal":
                                case "play_channel":
                                case "cut_off":
                                    event.setStatus(1);
                                    magEventRepository.save(event);
                            }
                        } else {
                            data.put("msgs", 0).put("additional_services_on", 1);
                        }
                        response.put("js", data);
                        break;
                    case "confirm_event":
                        if (eventActiveId != null && eventActiveId > 0)
                            magEventRepository.findById(eventActiveId).ifPresent(event -> {
                                event.setStatus(1);
                                magEventRepository.save(event);
                            });
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
                    case "get_terms_info":
                    case "get_payment_info":
                    case "get_demo_video_parts":
                    case "get_agreement_info":
                        response.put("js", true);
                        break;
                    case "get_main_info":
                        response.put("js", new JSONObject().put("mac", ""));
                        break;
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
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("cache-control", "no-store, no-cache, must-revalidate");
        responseHeaders.add("cache-control", "post-check=0, pre-check=0");
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        responseHeaders.setPragma("no-cache");

        return new ResponseEntity<>(response.toString(), responseHeaders, HttpStatus.OK);

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

    private JSONObject getProfile(boolean doContinue) {
        String locale = "";
        //may need to change later
        int status = doContinue ? 0 : 1;
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
                .put("ls", JSONObject.NULL)
                .put("version", "")
                .put("lang", "")
                .put("locale", locale)
                .put("city_id", "")
                .put("hd", "")
                .put("main_notify", "")
                .put("fav_itv_on", "")
                .put("now_playing_start", JSONObject.NULL)
                .put("now_playing_type", "")
                .put("now_playing_content", JSONObject.NULL)
                .put("additional_services_on", "1")
                .put("time_last_play_tv", "0000-00-00 00:00:00")
                .put("time_last_play_video", "0000-00-00 00:00:00")
                .put("operator_id", "0")
                .put("storage_name", "")
                .put("hd_content", "0")
                .put("image_version", JSONObject.NULL)
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
                .put("comment", JSONObject.NULL)
                .put("now_playing_link_id", JSONObject.NULL)
                .put("now_playing_streamer_id", JSONObject.NULL)
                .put("just_started", "1")
                .put("last_watchdog", "2018-02-18 17:33:39")
                .put("created", "2018-02-18 14:40:12")
                .put("plasma_saving", "0")
                .put("ts_enabled", "0")
                .put("ts_enable_icon", "1")
                .put("ts_path", JSONObject.NULL)
                .put("ts_max_length", "3600")
                .put("ts_buffer_use", "cyclic")
                .put("ts_action_on_exit", "no_save")
                .put("ts_delay", "on_pause")
                .put("video_clock", "Off")
                .put("verified", "0")
                .put("hdmi_event_reaction", "1")
                .put("pri_audio_lang", "")
                .put("sec_audio_lang", "")
                .put("pri_subtitle_lang", "")
                .put("sec_subtitle_lang", "")
                .put("subtitle_color", "16777215")
                .put("subtitle_size", "20")
                .put("show_after_loading", "")
                .put("play_in_preview_by_ok", "")
                .put("hw_version", JSONObject.NULL)
                .put("openweathermap_city_id", "0")
                .put("theme", "")
                .put("settings_password", "0000")
                .put("expire_billing_date", "0000-00-00 00:00:00")
                .put("reseller_id", JSONObject.NULL)
                .put("account_balance", "")
                .put("client_type", "STB")
                .put("hw_version_2", "62")
                .put("blocked", "0")
                .put("units", "metric")
                .put("tariff_expired_date", JSONObject.NULL)
                .put("tariff_id_instead_expired", JSONObject.NULL)
                .put("activation_code_auto_issue", "1")
                .put("last_itv_id", 0)
//                .put("updated", new JSONArray().put(new JSONObject().put("id", "1")).put(new JSONObject().put("anec", "0")).put(new JSONObject().put("vclub", "0")))
                .put("updated", new JSONObject().put("id", "1").put("uid", "1").put("anec", "0").put("vclub", "0"))
                .put("rtsp_type", "4")
                .put("rtsp_flags", "0")
                .put("stb_lang", "en")
                .put("display_menu_after_loading", "")
                .put("record_max_length", "180")
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
                .put("cas_params", JSONObject.NULL)
                .put("cas_web_params", JSONObject.NULL)
                .put("cas_additional_params", new JSONArray())
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
                .put("play_in_preview_only_by_ok", "false")
                .put("enable_stream_error_logging", "")
                .put("always_enabled_subtitles", alwaysEnabledSubtitles ? "1" : "0")
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
                .put("hide_tv_genres_in_fullscreen", JSONObject.NULL)
                .put("advert", JSONObject.NULL)
                .put("status", status)
                //read these from setting
                .put("update_url", "")
                .put("test_download_url", "")
                .put("default_timezone", "")
                .put("default_locale", "")
                .put("allowed_stb_types", new JSONArray().putAll(Arrays.asList("aurahd", "aurahd2", "aurahd3", "aurahd4", "aurahd5", "aurahd6", "aurahd7", "aurahd8", "aurahd9", "mag200", "mag245", "mag245d", "mag250", "mag254", "mag255", "mag256", "mag257", "mag260", "mag270", "mag275", "mag322", "mag323", "mag324", "mag325", "mag349", "mag350", "mag351", "mag352", "mag420", "wr320")))
                .put("allowed_stb_types_for_local_recording", new JSONArray().putAll(Arrays.asList("aurahd", "aurahd2", "aurahd3", "aurahd4", "aurahd5", "aurahd6", "aurahd7", "aurahd8", "aurahd9", "mag200", "mag245", "mag245d", "mag250", "mag254", "mag255", "mag256", "mag257", "mag260", "mag270", "mag275", "mag322", "mag323", "mag324", "mag325", "mag349", "mag350", "mag351", "mag352", "mag420", "wr320")))
                .put("storages", new JSONArray())
                .put("tv_channel_default_aspect", "")
                .put("playback_limit", "")
                .put("enable_playback_limit", "")
                .put("show_tv_channel_logo", true)
                .put("show_channel_logo_in_preview", true)
                .put("enable_connection_problem_indication", "")
                .put("hls_fast_start", "1")
                .put("check_ssl_certificate", 0)
                .put("enable_buffering_indication", 1)
                .put("watchdog_timeout", new Random().nextInt(40) + 80)
                //read from setting?
                .put("aspect", "");
    }

    private Optional<MagDevice> authenticateMagDevice(String token, String serialNumber, String mac, String version, String stbType, String imageVersion, String deviceId, String deviceId2, String hwVersion, String userIp) {
        Optional<MagDevice> magRecord;
        if (!StringUtils.isBlank(mac)) {
            magRecord = repository.findByMac(mac);
        } else if (!StringUtils.isBlank(token)) {
            token = token.replace("Bearer ", "");
            magRecord = repository.findByToken(token);
        } else return Optional.empty();
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

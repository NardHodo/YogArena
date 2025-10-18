package com.example.yogarena;

import java.util.HashMap;
import java.util.Map;

public class PoseMasterList {

    // This Map is your "static database"
    public static final Map<String, PoseInfo> ALL_POSES = new HashMap<>();

    // This 'static' block runs once when your app starts
    static {
        // Manually add all your poses here.
        // The first string MUST match your classifier's label (the poseId).

        // Format:
        // ALL_POSES.put("classifier_label_name",
        //     new PoseInfo("classifier_label_name", "Simple Name", R.drawable.your_image, "Category Name"));

        ALL_POSES.put("Akarna_Dhanurasana",
                new PoseInfo("Akarna_Dhanurasana", "Archer's Pose", R.drawable.akarna, "Sitting (Twist)"));

        ALL_POSES.put("Bharadvaja's_Twist_pose_or_Bharadvajasana_I_",
                new PoseInfo("Bharadvaja's_Twist_pose_or_Bharadvajasana_I_", "Bharadvaja's Twist", R.drawable.yoga_pose_1, "Sitting (Legs Behind)"));

        ALL_POSES.put("Boat_Pose_or_Paripurna_Navasana_",
                new PoseInfo("Boat_Pose_or_Paripurna_Navasana_", "Boat Pose", R.drawable.yoga_pose_1, "Wheel (Others)"));

        ALL_POSES.put("Bound_Angle_Pose_or_Baddha_Konasana_",
                new PoseInfo("Bound_Angle_Pose_or_Baddha_Konasana_", "Bound Angle Pose", R.drawable.yoga_pose_1, "Sitting (Legs in Front)"));

        ALL_POSES.put("Bow_Pose_or_Dhanurasana_",
                new PoseInfo("Bow_Pose_or_Dhanurasana_", "Bow Pose", R.drawable.yoga_pose_1, "Wheel (Up-facing)"));

        ALL_POSES.put("Bridge_Pose_or_Setu_Bandha_Sarvangasana_",
                new PoseInfo("Bridge_Pose_or_Setu_Bandha_Sarvangasana_", "Bridge Pose", R.drawable.yoga_pose_1, "Wheel (Up-facing)"));

        ALL_POSES.put("Camel_Pose_or_Ustrasana_",
                new PoseInfo("Camel_Pose_or_Ustrasana_", "Camel Pose", R.drawable.yoga_pose_1, "Wheel (Up-facing)"));

        ALL_POSES.put("Cat_Cow_Pose_or_Marjaryasana_",
                new PoseInfo("Cat_Cow_Pose_or_Marjaryasana_", "Cat-Cow Pose", R.drawable.yoga_pose_1, "Wheel (Down-Facing)"));

        ALL_POSES.put("Chair_Pose_or_Utkatasana_",
                new PoseInfo("Chair_Pose_or_Utkatasana_", "Chair Pose", R.drawable.yoga_pose_1, "Standing (Straight)"));

        ALL_POSES.put("Child_Pose_or_Balasana_",
                new PoseInfo("Child_Pose_or_Balasana_", "Child's Pose", R.drawable.yoga_pose_1, "Reclining (Down-facing)"));

        ALL_POSES.put("Cobra_Pose_or_Bhujangasana_",
                new PoseInfo("Cobra_Pose_or_Bhujangasana_", "Cobra Pose", R.drawable.yoga_pose_1, "Reclining (Down-facing)"));

        ALL_POSES.put("Cockerel_Pose",
                new PoseInfo("Cockerel_Pose", "Cockerel Pose (Kukkutasana)", R.drawable.yoga_pose_1, "Balancing (Front)"));

        ALL_POSES.put("Corpse_Pose_or_Savasana_",
                new PoseInfo("Corpse_Pose_or_Savasana_", "Corpse Pose", R.drawable.yoga_pose_1, "Reclining (Up-facing)"));

        ALL_POSES.put("Cow_Face_Pose_or_Gomukhasana_",
                new PoseInfo("Cow_Face_Pose_or_Gomukhasana_", "Cow Face Pose", R.drawable.yoga_pose_1, "Sitting (Legs Behind)"));

        ALL_POSES.put("Crane_(Crow)_Pose_or_Bakasana_",
                new PoseInfo("Crane_(Crow)_Pose_or_Bakasana_", "Crane (Crow) Pose", R.drawable.yoga_pose_1, "Balancing (Front)"));

        ALL_POSES.put("Dolphin_Plank_Pose_or_Makara_Adho_Mukha_Svanasana_",
                new PoseInfo("Dolphin_Plank_Pose_or_Makara_Adho_Mukha_Svanasana_", "Dolphin Plank Pose", R.drawable.yoga_pose_1, "Reclining (Plank Balance)"));

        ALL_POSES.put("Dolphin_Pose_or_Ardha_Pincha_Mayurasana_",
                new PoseInfo("Dolphin_Pose_or_Ardha_Pincha_Mayurasana_", "Dolphin Pose", R.drawable.yoga_pose_1, "Standing (Forward Bend)"));

        ALL_POSES.put("Downward-Facing_Dog_pose_or_Adho_Mukha_Svanasana_",
                new PoseInfo("Downward-Facing_Dog_pose_or_Adho_Mukha_Svanasana_", "Downward-Facing Dog", R.drawable.yoga_pose_1, "Standing (Forward Bend)"));

        ALL_POSES.put("Eagle_Pose_or_Garudasana_",
                new PoseInfo("Eagle_Pose_or_Garudasana_", "Eagle Pose", R.drawable.yoga_pose_1, "Standing (Straight)"));

        ALL_POSES.put("Eight-Angle_Pose_or_Astavakrasana_",
                new PoseInfo("Eight-Angle_Pose_or_Astavakrasana_", "Eight-Angle Pose", R.drawable.yoga_pose_1, "Balancing (Front)"));

        ALL_POSES.put("Extended_Puppy_Pose_or_Uttana_Shishosana_",
                new PoseInfo("Extended_Puppy_Pose_or_Uttana_Shishosana_", "Extended Puppy Pose", R.drawable.yoga_pose_1, "Reclining (Down-facing)"));

        ALL_POSES.put("Extended_Revolved_Side_Angle_Pose_or_Utthita_Parsvakonasana_",
                new PoseInfo("Extended_Revolved_Side_Angle_Pose_or_Utthita_Parsvakonasana_", "Extended Revolved Side Angle Pose", R.drawable.yoga_pose_1, "Standing (Side Bend)"));

        ALL_POSES.put("Extended_Revolved_Triangle_Pose_or_Utthita_Trikonasana_",
                new PoseInfo("Extended_Revolved_Triangle_Pose_or_Utthita_Trikonasana_", "Extended Revolved Triangle Pose", R.drawable.yoga_pose_1, "Standing (Side Bend)"));

        ALL_POSES.put("Feathered_Peacock_Pose_or_Pincha_Mayurasana_",
                new PoseInfo("Feathered_Peacock_Pose_or_Pincha_Mayurasana_", "Feathered Peacock Pose", R.drawable.yoga_pose_1, "Inverted (Legs Straight Up)"));

        ALL_POSES.put("Firefly_Pose_or_Tittibhasana_",
                new PoseInfo("Firefly_Pose_or_Tittibhasana_", "Firefly Pose", R.drawable.yoga_pose_1, "Balancing (Front)"));

        ALL_POSES.put("Fish_Pose_or_Matsyasana_",
                new PoseInfo("Fish_Pose_or_Matsyasana_", "Fish Pose", R.drawable.yoga_pose_1, "Reclining (Up-facing)"));

        ALL_POSES.put("Four-Limbed_Staff_Pose_or_Chaturanga_Dandasana_",
                new PoseInfo("Four-Limbed_Staff_Pose_or_Chaturanga_Dandasana_", "Four-Limbed Staff Pose", R.drawable.yoga_pose_1, "Reclining (Plank Balance)"));

        ALL_POSES.put("Frog_Pose_or_Bhekasana",
                new PoseInfo("Frog_Pose_or_Bhekasana", "Frog Pose", R.drawable.yoga_pose_1, "Reclining (Down-facing)"));

        ALL_POSES.put("Garland_Pose_or_Malasana_",
                new PoseInfo("Garland_Pose_or_Malasana_", "Garland Pose", R.drawable.yoga_pose_1, "Sitting (Legs in Front)"));

        ALL_POSES.put("Gate_Pose_or_Parighasana_",
                new PoseInfo("Gate_Pose_or_Parighasana_", "Gate Pose", R.drawable.yoga_pose_1, "Standing (Side Bend)"));

        ALL_POSES.put("Half_Lord_of_the_Fishes_Pose_or_Ardha_Matsyendrasana_",
                new PoseInfo("Half_Lord_of_the_Fishes_Pose_or_Ardha_Matsyendrasana_", "Half Lord of the Fishes Pose", R.drawable.yoga_pose_1, "Sitting (Legs Behind)"));

        ALL_POSES.put("Half_Moon_Pose_or_Ardha_Chandrasana_",
                new PoseInfo("Half_Moon_Pose_or_Ardha_Chandrasana_", "Half Moon Pose", R.drawable.yoga_pose_1, "Standing (Side Bend)"));

        ALL_POSES.put("Handstand_pose_or_Adho_Mukha_Vrksasana_",
                new PoseInfo("Handstand_pose_or_Adho_Mukha_Vrksasana_", "Handstand", R.drawable.yoga_pose_1, "Inverted (Legs Straight Up)"));

        ALL_POSES.put("Happy_Baby_Pose_or_Ananda_Balasana_",
                new PoseInfo("Happy_Baby_Pose_or_Ananda_Balasana_", "Happy Baby Pose", R.drawable.yoga_pose_1, "Reclining (Up-facing)"));

        ALL_POSES.put("Head-to-Knee_Forward_Bend_pose_or_Janu_Sirsasana_",
                new PoseInfo("Head-to-Knee_Forward_Bend_pose_or_Janu_Sirsasana_", "Head-to-Knee Forward Bend", R.drawable.yoga_pose_1, "Sitting (Forward Bend)"));

        ALL_POSES.put("Heron_Pose_or_Krounchasana_",
                new PoseInfo("Heron_Pose_or_Krounchasana_", "Heron Pose", R.drawable.yoga_pose_1, "Sitting (Twist)"));

        ALL_POSES.put("Intense_Side_Stretch_Pose_or_Parsvottanasana_",
                new PoseInfo("Intense_Side_Stretch_Pose_or_Parsvottanasana_", "Intense Side Stretch Pose", R.drawable.yoga_pose_1, "Standing (Forward Bend)"));

        ALL_POSES.put("Legs-Up-the-Wall_Pose_or_Viparita_Karani_",
                new PoseInfo("Legs-Up-the-Wall_Pose_or_Viparita_Karani_", "Legs-Up-the-Wall Pose", R.drawable.yoga_pose_1, "Inverted (Legs Straight Up)"));

        ALL_POSES.put("Locust_Pose_or_Salabhasana_",
                new PoseInfo("Locust_Pose_or_Salabhasana_", "Locust Pose", R.drawable.yoga_pose_1, "Reclining (Down-facing)"));

        ALL_POSES.put("Lord_of_the_Dance_Pose_or_Natarajasana_",
                new PoseInfo("Lord_of_the_Dance_Pose_or_Natarajasana_", "Lord of the Dance Pose", R.drawable.yoga_pose_1, "Standing (Others)"));

        ALL_POSES.put("Low_Lunge_pose_or_Anjaneyasana_",
                new PoseInfo("Low_Lunge_pose_or_Anjaneyasana_", "Low Lunge", R.drawable.yoga_pose_1, "Standing (Side Bend)"));

        ALL_POSES.put("Noose_Pose_or_Pasasana_",
                new PoseInfo("Noose_Pose_or_Pasasana_", "Noose Pose", R.drawable.yoga_pose_1, "Sitting (Legs in Front)"));

        ALL_POSES.put("Peacock_Pose_or_Mayurasana_",
                new PoseInfo("Peacock_Pose_or_Mayurasana_", "Peacock Pose", R.drawable.yoga_pose_1, "Reclining (Plank Balance)"));

        ALL_POSES.put("Pigeon_Pose_or_Kapotasana_",
                new PoseInfo("Pigeon_Pose_or_Kapotasana_", "Pigeon Pose (One Legged King)", R.drawable.yoga_pose_1, "Wheel (Up-facing)"));

        ALL_POSES.put("Plank_Pose_or_Kumbhakasana_",
                new PoseInfo("Plank_Pose_or_Kumbhakasana_", "Plank Pose", R.drawable.yoga_pose_1, "Reclining (Plank Balance)"));

        ALL_POSES.put("Plow_Pose_or_Halasana_",
                new PoseInfo("Plow_Pose_or_Halasana_", "Plow Pose", R.drawable.yoga_pose_1, "Inverted (Legs Behind)"));

        ALL_POSES.put("Pose_Dedicated_to_the_Sage_Koundinya_or_Eka_Pada_Koundinyanasana_I_and_II",
                new PoseInfo("Pose_Dedicated_to_the_Sage_Koundinya_or_Eka_Pada_Koundinyanasana_I_and_II", "Sage Koundinya's Pose", R.drawable.yoga_pose_1, "Balancing (Front)"));

        ALL_POSES.put("Rajakapotasana",
                new PoseInfo("Rajakapotasana", "King Pigeon Pose", R.drawable.yoga_pose_1, "Sitting (Twist)"));

        ALL_POSES.put("Reclining_Hand-to-Big-Toe_Pose_or_Supta_Padangusthasana_",
                new PoseInfo("Reclining_Hand-to-Big-Toe_Pose_or_Supta_Padangusthasana_", "Reclining Hand-to-Big-Toe Pose", R.drawable.yoga_pose_1, "Reclining (Up-facing)"));

        ALL_POSES.put("Revolved_Head-to-Knee_Pose_or_Parivrtta_Janu_Sirsasana_",
                new PoseInfo("Revolved_Head-to-Knee_Pose_or_Parivrtta_Janu_Sirsasana_", "Revolved Head-to-Knee Pose", R.drawable.yoga_pose_1, "Sitting (Forward Bend)"));

        ALL_POSES.put("Scale_Pose_or_Tolasana_",
                new PoseInfo("Scale_Pose_or_Tolasana_", "Scale Pose", R.drawable.yoga_pose_1, "Balancing (Front)"));

        ALL_POSES.put("Scorpion_pose_or_vrischikasana",
                new PoseInfo("Scorpion_pose_or_vrischikasana", "Scorpion Pose", R.drawable.yoga_pose_1, "Inverted (Legs Behind)"));

        ALL_POSES.put("Seated_Forward_Bend_pose_or_Paschimottanasana_",
                new PoseInfo("Seated_Forward_Bend_pose_or_Paschimottanasana_", "Seated Forward Bend", R.drawable.yoga_pose_1, "Sitting (Forward Bend)"));

        ALL_POSES.put("Shoulder-Pressing_Pose_or_Bhujapidasana_",
                new PoseInfo("Shoulder-Pressing_Pose_or_Bhujapidasana_", "Shoulder-Pressing Pose", R.drawable.yoga_pose_1, "Balancing (Front)"));

        ALL_POSES.put("Side-Reclining_Leg_Lift_pose_or_Anantasana_",
                new PoseInfo("Side-Reclining_Leg_Lift_pose_or_Anantasana_", "Side-Reclining Leg Lift", R.drawable.yoga_pose_1, "Reclining (Side facing)"));

        ALL_POSES.put("Side_Crane_(Crow)_Pose_or_Parsva_Bakasana_",
                new PoseInfo("Side_Crane_(Crow)_Pose_or_Parsva_Bakasana_", "Side Crane (Crow) Pose", R.drawable.yoga_pose_1, "Balancing (Side)"));

        ALL_POSES.put("Side_Plank_Pose_or_Vasisthasana_",
                new PoseInfo("Side_Plank_Pose_or_Vasisthasana_", "Side Plank Pose", R.drawable.yoga_pose_1, "Reclining (Side facing)"));

        ALL_POSES.put("Sitting_pose_1_(normal)",
                new PoseInfo("Sitting_pose_1_(normal)", "Easy Pose (Sukhasana)", R.drawable.yoga_pose_1, "Sitting (Legs in Front)"));

        ALL_POSES.put("Split_pose",
                new PoseInfo("Split_pose", "Split Pose (Hanumanasana)", R.drawable.yoga_pose_1, "Sitting (Split)"));

        ALL_POSES.put("Staff_Pose_or_Dandasana_",
                new PoseInfo("Staff_Pose_or_Dandasana_", "Staff Pose", R.drawable.yoga_pose_1, "Sitting (Legs in Front)"));

        ALL_POSES.put("Standing_Forward_Bend_pose_or_Uttanasana_",
                new PoseInfo("Standing_Forward_Bend_pose_or_Uttanasana_", "Standing Forward Bend", R.drawable.yoga_pose_1, "Standing (Forward Bend)"));

        ALL_POSES.put("Standing_Split_pose_or_Urdhva_Prasarita_Eka_Padasana_",
                new PoseInfo("Standing_Split_pose_or_Urdhva_Prasarita_Eka_Padasana_", "Standing Split", R.drawable.yoga_pose_1, "Standing (Others)"));

        ALL_POSES.put("Standing_big_toe_hold_pose_or_Utthita_Padangusthasana",
                new PoseInfo("Standing_big_toe_hold_pose_or_Utthita_Padangusthasana", "Standing Hand to Big Toe Pose", R.drawable.yoga_pose_1, "Standing (Others)"));

        ALL_POSES.put("Supported_Headstand_pose_or_Salamba_Sirsasana_",
                new PoseInfo("Supported_Headstand_pose_or_Salamba_Sirsasana_", "Supported Headstand", R.drawable.yoga_pose_1, "Inverted (Legs Straight Up)"));

        ALL_POSES.put("Supported_Shoulderstand_pose_or_Salamba_Sarvangasana_",
                new PoseInfo("Supported_Shoulderstand_pose_or_Salamba_Sarvangasana_", "Supported Shoulderstand", R.drawable.yoga_pose_1, "Inverted (Legs Straight Up)"));

        ALL_POSES.put("Supta_Baddha_Konasana_",
                new PoseInfo("Supta_Baddha_Konasana_", "Reclining Bound Angle Pose", R.drawable.yoga_pose_1, "Reclining (Up-facing)"));

        ALL_POSES.put("Supta_Virasana_Vajrasana",
                new PoseInfo("Supta_Virasana_Vajrasana", "Reclining Hero Pose", R.drawable.yoga_pose_1, "Reclining (Up-facing)"));

        ALL_POSES.put("Tortoise_Pose",
                new PoseInfo("Tortoise_Pose", "Tortoise Pose (Kurmasana)", R.drawable.yoga_pose_1, "Sitting (Forward Bend)"));

        ALL_POSES.put("Tree_Pose_or_Vrksasana_",
                new PoseInfo("Tree_Pose_or_Vrksasana_", "Tree Pose", R.drawable.yoga_pose_1, "Standing (Straight)"));

        ALL_POSES.put("Upward_Bow_(Wheel)_Pose_or_Urdhva_Dhanurasana_",
                new PoseInfo("Upward_Bow_(Wheel)_Pose_or_Urdhva_Dhanurasana_", "Upward Bow (Wheel) Pose", R.drawable.yoga_pose_1, "Wheel (Up-facing)"));

        ALL_POSES.put("Upward_Facing_Two-Foot_Staff_Pose_or_Dwi_Pada_Viparita_Dandasana_",
                new PoseInfo("Upward_Facing_Two-Foot_Staff_Pose_or_Dwi_Pada_Viparita_Dandasana_", "Upward Facing Two-Foot Staff Pose", R.drawable.yoga_pose_1, "Wheel (Up-facing)"));

        ALL_POSES.put("Upward_Plank_Pose_or_Purvottanasana_",
                new PoseInfo("Upward_Plank_Pose_or_Purvottanasana_", "Upward Plank Pose", R.drawable.yoga_pose_1, "Wheel (Up-facing)"));

        ALL_POSES.put("Virasana_or_Vajrasana",
                new PoseInfo("Virasana_or_Vajrasana", "Hero Pose / Thunderbolt Pose", R.drawable.yoga_pose_1, "Sitting (Legs Behind)"));

        ALL_POSES.put("Warrior_III_Pose_or_Virabhadrasana_III_",
                new PoseInfo("Warrior_III_Pose_or_Virabhadrasana_III_", "Warrior III Pose", R.drawable.yoga_pose_1, "Standing (Others)"));

        ALL_POSES.put("Warrior_II_Pose_or_Virabhadrasana_II_",
                new PoseInfo("Warrior_II_Pose_or_Virabhadrasana_II_", "Warrior II Pose", R.drawable.yoga_pose_1, "Standing Poses"));

        ALL_POSES.put("Warrior_I_Pose_or_Virabhadrasana_I_",
                new PoseInfo("Warrior_I_Pose_or_Virabhadrasana_I_", "Warrior I Pose", R.drawable.yoga_pose_1, "Standing (Side Bend)"));

        ALL_POSES.put("Wide-Angle_Seated_Forward_Bend_pose_or_Upavistha_Konasana_",
                new PoseInfo("Wide-Angle_Seated_Forward_Bend_pose_or_Upavistha_Konasana_", "Wide-Angle Seated Forward Bend", R.drawable.yoga_pose_1, "Sitting (Split)"));

        ALL_POSES.put("Wide-Legged_Forward_Bend_pose_or_Prasarita_Padottanasana_",
                new PoseInfo("Wide-Legged_Forward_Bend_pose_or_Prasarita_Padottanasana_", "Wide-Legged Forward Bend", R.drawable.yoga_pose_1, "Standing (Forward Bend)"));

        ALL_POSES.put("Wild_Thing_pose_or_Camatkarasana_",
                new PoseInfo("Wild_Thing_pose_or_Camatkarasana_", "Wild Thing Pose", R.drawable.yoga_pose_1, "Wheel (Up-facing)"));

        ALL_POSES.put("Wind_Relieving_pose_or_Pawanmuktasana",
                new PoseInfo("Wind_Relieving_pose_or_Pawanmuktasana", "Wind Relieving Pose", R.drawable.yoga_pose_1, "Reclining (Up-facing)"));

        ALL_POSES.put("Yogic_sleep_pose",
                new PoseInfo("Yogic_sleep_pose", "Yogic Sleep (Yoga Nidrasana)", R.drawable.yoga_pose_1, "Reclining (Up-facing)"));

        ALL_POSES.put("viparita_virabhadrasana_or_reverse_warrior_pose",
                new PoseInfo("viparita_virabhadrasana_or_reverse_warrior_pose", "Reverse Warrior Pose", R.drawable.yoga_pose_1, "Standing (Side Bend)"));
        // ... YOU MUST ADD ALL YOUR OTHER 80+ POSES HERE
        // ...
    }

    // A helper to easily get info for any pose by its ID
    public static PoseInfo getInfo(String poseId) {
        return ALL_POSES.get(poseId);
    }
}
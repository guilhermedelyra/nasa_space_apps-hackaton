using UnityEngine;
using System;
using UnityEngine.UI;
using TMPro;

public class CollisionGameObject : MonoBehaviour
{
    public GameObject canvas;
    public TextMeshProUGUI radiation, member_title;
    public Image bar_radiation;

    void OnTriggerEnter(Collider collision)
    {
        Debug.Log("entrou " + collision.gameObject.tag);
        if (collision.gameObject.tag == "maodedo")
        {
            canvas.SetActive(!canvas.activeSelf);
            if (canvas.activeSelf) {
                System.Random rnd = new System.Random();
                int random = rnd.Next(0, 7000);
                radiation.text = random.ToString() + "sV";
                float hue = (255*random)/7000;
                bar_radiation.fillAmount = hue/255;
                radiation.color = new Color32((byte)(hue), (byte)(255 - hue), 0, 255);
                member_title.text = char.ToUpper(gameObject.name.Split('_')[1][0])+gameObject.name.Split('_')[1].Substring(1);
            }
        }
    }
}